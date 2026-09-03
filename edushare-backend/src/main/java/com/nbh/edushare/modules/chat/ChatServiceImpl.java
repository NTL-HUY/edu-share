package com.nbh.edushare.modules.chat;

import com.nbh.edushare.common.exception.AppException;
import com.nbh.edushare.modules.chat.dto.RoomUnreadProjection;
import com.nbh.edushare.modules.chat.dto.request.CursorPaginateRequest;
import com.nbh.edushare.modules.chat.dto.request.IncomingChatMessage;
import com.nbh.edushare.modules.chat.dto.request.SendMessageRequest;
import com.nbh.edushare.modules.chat.dto.response.ChatAckMessage;
import com.nbh.edushare.modules.chat.dto.response.CursorPagingResponse;
import com.nbh.edushare.modules.chat.enums.AckStatus;
import com.nbh.edushare.modules.chat.event.ChatMessageEvent;
import com.nbh.edushare.modules.chat.pojo.ChatMessage;
import com.nbh.edushare.modules.chat.pojo.ChatRoom;
import com.nbh.edushare.modules.chat.pojo.RoomReadState;
import com.nbh.edushare.modules.chat.repository.ChatMessageRepository;
import com.nbh.edushare.modules.chat.repository.ChatRoomRepository;
import com.nbh.edushare.modules.chat.repository.RoomReadStateRepository;
import com.nbh.edushare.modules.chat.util.ChatMessageBuilder;
import com.nbh.edushare.modules.user.UserService;
import com.nbh.edushare.modules.user.dto.response.UserBaseProjection;
import com.nbh.edushare.modules.user.dto.response.UserRoleProjection;
import com.nbh.edushare.modules.user.enums.UserRole;
import com.nbh.edushare.modules.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private static final int PREVIEW_MAX_LENGTH = 200;
    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;
    private final ChatWebSocketService chatWebSocketService;
    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;
    private final ChatMessageRepository chatMessageRepository;
    private final RoomReadStateRepository roomReadStateRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ChatMapper chatMapper;
    private final ChatMessageBuilder chatMessageBuilder;


    @Override
    @Transactional
    public ChatMessage sendMessage(Long roomId, Long userId, SendMessageRequest request) {
        ChatRoom room = chatRoomRepository.findByIdAndIsActiveTrue(roomId)
                .orElseThrow(() -> new AppException("Nhóm này không còn tồn tại"));

        UserBaseProjection currentUser = userService.findProjectedById(userId, UserBaseProjection.class)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

        ChatMessageBuilder.BuildResult result = chatMessageBuilder.build(
                room.getId(), currentUser, request.content(), request.replyToMessageId(), null
        );

        if (result.isFailed()) {
            throw new AppException(result.errorReason());
        }

        return chatMessageRepository.save(result.message());
    }

    @Override
    @Transactional
    public void markRead(Long roomId, Long userId, Long messageId) {
        UserBaseProjection currentUser = userService.findProjectedById(userId, UserBaseProjection.class)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
        int updated = roomReadStateRepository.updateLastReadIfGreater(roomId, currentUser.getId(), messageId);

        if (updated == 0) {
            boolean exists = roomReadStateRepository.findByRoomIdAndUserId(roomId, userId).isPresent();
            if (!exists) {
                RoomReadState state = new RoomReadState();
                state.setRoomId(roomId);
                state.setUserId(userId);
                state.setLastReadMessageId(messageId);
                roomReadStateRepository.save(state);
            }
        }
    }


    @Override
    @Transactional
    public void deleteMessage(Long roomId, Long userId, Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .filter(m -> m.getRoomId().equals(roomId))
                .orElseThrow(() -> new AppException("Tin nhắn không tồn tại"));
        if (message.getDeletedAt() != null) return;

        UserRoleProjection currentUser = userService.findProjectedById(userId, UserRoleProjection.class)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

        boolean isOwner = message.getUserId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getUserRole().equals(UserRole.ADMIN);

        if (!isOwner && !isAdmin) {
            throw new AppException("Bạn không có quyền xóa tin nhắn này");
        }

        message.setDeletedAt(LocalDateTime.now());
        message.setDeletedBy(currentUser.getId());
        chatMessageRepository.save(message);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Long> getUnreadCounts(Long userId) {
        Map<Long, Long> result = new HashMap<>();
        for (RoomUnreadProjection row : roomReadStateRepository.getUnreadCountsByUserId(userId)) {
            result.put(row.getRoomId(), row.getUnreadCount());
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPagingResponse<ChatMessage> getMessages(Long roomId, CursorPaginateRequest request) {
        Pageable pageable = PageRequest.of(0, request.limit() + 1);
        List<ChatMessage> items = chatMessageRepository.findMessagesByCursor(roomId, request.beforeId(), pageable);

        boolean hasMore = items.size() > request.limit();
        if (hasMore) {
            items = items.subList(0, request.limit());
        }

        Long nextBeforeId = items.isEmpty() ? null : items.getLast().getId();
        return CursorPagingResponse.of(items, nextBeforeId, hasMore);
    }

    @Override
    @Transactional(readOnly = true)
    public void handleStompSendMessage(IncomingChatMessage payload, Long userId) {
        boolean roomExists = chatRoomRepository.existsByIdAndIsActiveTrue(payload.roomId());

        if (!roomExists) {
            chatWebSocketService.sendAckToUser(userId, payload.clientTempId(), AckStatus.FAILED, "Phòng không tồn tại hoặc bạn không có quyền");
            return;
        }

        ChatMessageEvent event = chatMapper.toChatMessageEvent(payload, userId);

        kafkaTemplate.send(ChatKafkaConfig.CHAT_SEND_MESSAGE_TOPIC, payload.roomId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        chatWebSocketService.sendAckToUser(userId, payload.clientTempId(), AckStatus.FAILED, "Không gửi được, thử lại");
                    } else {
                        chatWebSocketService.sendAckToUser(userId, payload.clientTempId(), AckStatus.PENDING, null);
                    }
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoom> getChatRooms() {
        return chatRoomRepository.findByIsActiveTrue();
    }

    private String buildPreview(String content) {
        if (content == null) return null;
        String trimmed = content.trim();
        return trimmed.length() <= PREVIEW_MAX_LENGTH
                ? trimmed
                : trimmed.substring(0, PREVIEW_MAX_LENGTH - 3) + "...";
    }


}

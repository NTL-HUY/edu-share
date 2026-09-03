package com.nbh.edushare.modules.chat.listener;

import com.nbh.edushare.common.exception.AppException;
import com.nbh.edushare.modules.chat.ChatKafkaConfig;
import com.nbh.edushare.modules.chat.ChatMapper;
import com.nbh.edushare.modules.chat.ChatWebSocketService;
import com.nbh.edushare.modules.chat.dto.response.NewMessageBroadcast;
import com.nbh.edushare.modules.chat.enums.AckStatus;
import com.nbh.edushare.modules.chat.event.ChatMessageEvent;
import com.nbh.edushare.modules.chat.pojo.ChatMessage;
import com.nbh.edushare.modules.chat.repository.ChatMessageRepository;
import com.nbh.edushare.modules.chat.util.ChatMessageBuilder;
import com.nbh.edushare.modules.user.UserService;
import com.nbh.edushare.modules.user.dto.response.UserBaseProjection;
import com.nbh.edushare.modules.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessageListener {
    private static final int PREVIEW_MAX_LENGTH = 100;

    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatWebSocketService chatWebSocketService;
    private final UserService userService;
    private final ChatMessageBuilder  chatMessageBuilder;

    @KafkaListener(topics = ChatKafkaConfig.CHAT_SEND_MESSAGE_TOPIC, groupId = ChatKafkaConfig.CHAT_MESSAGE_WRITER)
    @Transactional
    public void onSendMessage(ChatMessageEvent event) {

        if (chatMessageRepository.existsByClientTempIdAndUserId(event.clientTempId(), event.userId())) {
            log.warn("Sự kiện này đã xử lý, skip. clientTempId={}", event.clientTempId());
            return;
        }

        UserBaseProjection currentUser = userService.findProjectedById(event.userId(), UserBaseProjection.class)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

        ChatMessageBuilder.BuildResult result = chatMessageBuilder.build(
                event.roomId(), currentUser, event.content(), event.replyToMessageId(), event.clientTempId()
        );

        if (result.isFailed()) {
            chatWebSocketService.sendAckToUser(event.userId(), event.clientTempId(), AckStatus.FAILED, result.errorReason());
            return;
        }
// 🔴 GIẢ LẬP ĐỘ TRỄ 3 GIÂY ĐỂ TEST
        try {
            Thread.sleep(5000); // 3000ms = 3 giây
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        ChatMessage saved = chatMessageRepository.save(result.message());

//        chatWebSocketService.sendAckToUser(event.userId(), event.clientTempId(), AckStatus.SENT, null);

        messagingTemplate.convertAndSend(
                "/topic/room-" + saved.getRoomId(),
                saved
        );
    }

    private String buildPreview(String content) {
        if (content == null) return null;
        return content.length() <= PREVIEW_MAX_LENGTH
                ? content
                : content.substring(0, PREVIEW_MAX_LENGTH) + "...";
    }
}
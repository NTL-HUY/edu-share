package com.nbh.edushare.modules.chat;

import com.nbh.edushare.modules.chat.dto.request.CursorPaginateRequest;
import com.nbh.edushare.modules.chat.dto.request.SendMessageRequest;
import com.nbh.edushare.modules.chat.dto.response.CursorPagingResponse;
import com.nbh.edushare.modules.chat.pojo.ChatMessage;
import com.nbh.edushare.modules.chat.pojo.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ChatGraphQLController {
    private final ChatService chatService;

    @QueryMapping
    public CursorPagingResponse<ChatMessage> messages(
            @Argument Long roomId,
            @Argument CursorPaginateRequest request
    ) {
        return chatService.getMessages(roomId, request);
    }

    @BatchMapping(typeName = "ChatRoom")
    public Map<ChatRoom, Long> unreadCount(List<ChatRoom> rooms) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<Long, Long> countsByRoomId = chatService.getUnreadCounts(userId);

        return rooms.stream()
                .collect(Collectors.toMap(
                        room -> room,
                        room -> countsByRoomId.getOrDefault(room.getId(), 0L)
                ));
    }

    @MutationMapping
    public ChatMessage sendMessage(
            @Argument Long roomId,
            @Argument SendMessageRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        return chatService.sendMessage(roomId, userId, request);
    }

    @MutationMapping
    public Boolean markRead(
            @Argument Long roomId,
            @Argument Long messageId,
            @AuthenticationPrincipal Long userId
    ) {
        chatService.markRead(roomId, userId, messageId);
        return true;
    }

    @MutationMapping
    public Boolean deleteMessage(
            @Argument Long roomId,
            @Argument Long messageId,
            @AuthenticationPrincipal Long userId
    ) {
        chatService.deleteMessage(roomId, userId, messageId);
        return true;
    }

    @QueryMapping
    public List<ChatRoom> rooms(){
        return chatService.getChatRooms();
    }

}

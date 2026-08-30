package com.nbh.edushare.modules.chat;

import com.nbh.edushare.modules.chat.dto.request.CursorPaginateRequest;
import com.nbh.edushare.modules.chat.dto.request.SendMessageRequest;
import com.nbh.edushare.modules.chat.dto.response.CursorPagingResponse;
import com.nbh.edushare.modules.chat.pojo.ChatMessage;
import com.nbh.edushare.modules.chat.pojo.ChatRoom;

import java.util.List;
import java.util.Map;

public interface ChatService {
    ChatMessage sendMessage(Long roomId, Long userId, SendMessageRequest request);

    void markRead(Long roomId, Long userId, Long messageId);

    void deleteMessage(Long roomId, Long userId, Long messageId);

    Map<Long, Long> getUnreadCounts(Long userId);

    CursorPagingResponse<ChatMessage> getMessages(Long roomId, CursorPaginateRequest request);

    List<ChatRoom> getChatRooms();
}

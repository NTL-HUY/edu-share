package com.nbh.edushare.modules.chat.dto.response;

import com.nbh.edushare.modules.chat.pojo.ChatMessage;

import java.time.LocalDateTime;

public record NewMessageBroadcast(
        String type,
        Long messageId,
        Long senderId,
        String senderName,
        String senderAvatarUrl,
        String content,
        Long replyToMessageId,
        String replyToUserName,
        String replyToContentPreview,
        LocalDateTime createdAt,
        String clientTempId
) {
    public static NewMessageBroadcast from(ChatMessage saved) {
        return new NewMessageBroadcast(
                "NEW_MESSAGE",
                saved.getId(),
                saved.getUserId(),
                saved.getUserName(),
                saved.getUserAvatarUrl(),
                saved.getContent(),
                saved.getReplyToMessageId(),
                saved.getReplyToUserName(),
                saved.getReplyToContentPreview(),
                saved.getCreatedAt(),
                saved.getClientTempId()
        );
    }
}
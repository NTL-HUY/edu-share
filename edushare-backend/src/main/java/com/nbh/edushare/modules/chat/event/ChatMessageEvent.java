package com.nbh.edushare.modules.chat.event;

import java.time.LocalDateTime;

public record ChatMessageEvent(
        String clientTempId,
        Long roomId,
        Long userId,
        String content,
        Long replyToMessageId,
        LocalDateTime createdAt
) {
}
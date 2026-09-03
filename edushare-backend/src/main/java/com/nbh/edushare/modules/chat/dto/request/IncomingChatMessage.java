package com.nbh.edushare.modules.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record IncomingChatMessage(
        @NotBlank(message = "clientTempId không được để trống")
        String clientTempId,

        @NotNull(message = "roomId không được để trống")
        @Positive(message = "roomId phải là số dương")
        Long roomId,

        @NotBlank(message = "Nội dung tin nhắn không được để trống")
        @Size(max = 2000, message = "Nội dung không vượt quá 2000 ký tự")
        String content,

        @Positive(message = "replyToMessageId phải là số dương")
        Long replyToMessageId
) {
}
package com.nbh.edushare.modules.chat.dto.response;

import com.nbh.edushare.modules.chat.enums.AckStatus;

public record ChatAckMessage(
        String type,
        String clientTempId,
        String status,
        String reason
) {
    public static ChatAckMessage of(String clientTempId, AckStatus status, String reason) {
        return new ChatAckMessage("ACK", clientTempId, status.name(), reason == null ? "" : reason);
    }
}
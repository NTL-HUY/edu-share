package com.nbh.edushare.modules.chat;

import com.nbh.edushare.modules.chat.enums.AckStatus;

public interface ChatWebSocketService {
    void sendAckToUser(Long userId, String clientTempId, AckStatus status, String reason);
}

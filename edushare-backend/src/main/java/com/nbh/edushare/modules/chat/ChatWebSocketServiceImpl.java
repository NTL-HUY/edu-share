package com.nbh.edushare.modules.chat;

import com.nbh.edushare.modules.chat.dto.response.ChatAckMessage;
import com.nbh.edushare.modules.chat.enums.AckStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatWebSocketServiceImpl implements ChatWebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendAckToUser(Long userId, String clientTempId, AckStatus status, String reason) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(), "/queue/ack",
                ChatAckMessage.of(clientTempId, status, reason)
        );
    }
}

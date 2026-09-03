package com.nbh.edushare.modules.chat;

import com.nbh.edushare.modules.chat.dto.request.IncomingChatMessage;
import com.nbh.edushare.modules.chat.event.ChatMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatStompController {
    private final ChatService chatService;

    @MessageMapping("/chat.send")
    public void handleSend(IncomingChatMessage payload, Principal principal) {
        chatService.handleStompSendMessage(payload,Long.parseLong(principal.getName()));
    }


}
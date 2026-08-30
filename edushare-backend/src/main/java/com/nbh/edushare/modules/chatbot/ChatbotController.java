package com.nbh.edushare.modules.chatbot;

import com.nbh.edushare.modules.chatbot.dto.request.ChatRequest;
import com.nbh.edushare.modules.chatbot.dto.response.ChatResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatbotController {
    private final ChatbotService chatbotService;

    @PostMapping
    public ChatResponse chat(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ChatRequest request) {
        if(userId != null){
            request.setUserId(userId);
        }
        return chatbotService.chat(request);
    }
}

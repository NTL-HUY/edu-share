package com.nbh.edushare.modules.chatbot;

import com.nbh.edushare.modules.chatbot.dto.request.ChatRequest;
import com.nbh.edushare.modules.chatbot.dto.response.ChatResponse;

public interface ChatbotService {
    ChatResponse chat(ChatRequest request);
}

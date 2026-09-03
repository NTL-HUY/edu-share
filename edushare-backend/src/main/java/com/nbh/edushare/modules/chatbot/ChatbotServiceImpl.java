package com.nbh.edushare.modules.chatbot;

import com.nbh.edushare.common.exception.AppException;
import com.nbh.edushare.modules.chatbot.dto.request.ChatRequest;
import com.nbh.edushare.modules.chatbot.dto.response.ChatResponse;
import com.nbh.edushare.modules.chatbot.exception.ChatbotErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final RestClient pythonRagRestClient;

    @Override
    public ChatResponse chat(ChatRequest request) {

        try {
            return pythonRagRestClient.post()
                    .uri("/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        String body = readBody(res.getBody());
                        throw new AppException(ChatbotErrorCode.RAG_SERVICE_ERROR, body);
                    })
                    .body(ChatResponse.class);
        } catch (ResourceAccessException e) {
            // timeout / connection refused / không kết nối được Python service
            throw new AppException(ChatbotErrorCode.RAG_SERVICE_UNAVAILABLE);
        }
    }

    private String readBody(java.io.InputStream is) {
        try {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
}
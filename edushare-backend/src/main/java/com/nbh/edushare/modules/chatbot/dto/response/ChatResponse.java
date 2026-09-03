package com.nbh.edushare.modules.chatbot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String model;
    private String answer;
    private List<ChatSourceResponse> sources;
    private String prompt;
}
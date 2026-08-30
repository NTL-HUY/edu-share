package com.nbh.edushare.modules.chatbot.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSourceResponse {
    private Long knowledgeId;
    private String title;
    private String type;
    private Double similarity;
}
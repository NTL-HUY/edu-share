package com.nbh.edushare.modules.chatbot.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    @NotBlank(message = "Câu hỏi không được để trống")
    private String query;

    private Long userId;

    private List<Long> visibleOwnerIds;

    private String model;

    @Builder.Default
    private boolean stream = false;
}

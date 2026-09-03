package com.nbh.edushare.modules.knowledge.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateQuestionCommand(
    // --- Bảng cha: Knowledge ---
    @NotBlank(message = "Tiêu đề không được để trống") String title,

    String abstractText,

    String thumbnailUrl,

    @NotNull(message = "Trạng thái hiển thị (isPublic) không được để trống") Boolean isPublic,

    @NotNull(message = "Quyền bình luận (allowComment) không được để trống") Boolean allowComment,

    // --- Bảng con: Question ---
    @NotBlank(message = "Nội dung câu hỏi không được để trống") String content,

    Long categoryId
) {
}
package com.nbh.edushare.modules.knowledge.dto.command;

public record UpdateQuestionCommand(
        Long id,
        String title,
        String abstractText,
        String thumbnailUrl,
        Boolean isPublic,
        Boolean allowComment,
        Long categoryId,
        String content,
        Long acceptedAnswerId,
        Boolean isResolved
) {}
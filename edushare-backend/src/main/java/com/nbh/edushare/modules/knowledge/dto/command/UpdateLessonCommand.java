package com.nbh.edushare.modules.knowledge.dto.command;

import com.nbh.edushare.modules.knowledge.enums.LessonLevel;

public record UpdateLessonCommand(
        Long id,
        String title,
        String abstractText,
        String thumbnailUrl,
        Boolean isPublic,
        Boolean allowComment,
        Long categoryId,

        String contentMarkdown,
        LessonLevel level,
        Integer estimateTimeInMinutes
) {}
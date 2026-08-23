package com.nbh.edushare.modules.knowledge.dto.command;

import com.nbh.edushare.modules.knowledge.enums.LessonLevel;

public record CreateLessonCommand(
        String title,
        String abstractText,
        String thumbnailUrl,
        Boolean isPublic,
        Boolean allowComment,
        String contentMarkdown,
        LessonLevel level,
        Integer estimateTimeInMinutes,
        Long categoryId

) {
}

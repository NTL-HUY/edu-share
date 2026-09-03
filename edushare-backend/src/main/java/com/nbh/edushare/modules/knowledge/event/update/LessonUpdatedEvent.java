package com.nbh.edushare.modules.knowledge.event.update;

import com.nbh.edushare.modules.knowledge.enums.KnowledgeType;
import com.nbh.edushare.modules.knowledge.enums.LessonLevel;

import java.time.LocalDateTime;

public record LessonUpdatedEvent(
        Long knowledgeId,
        KnowledgeType type,
        Long ownerId,
        String title,
        String abstractText,
        String thumbnailUrl,
        Boolean isPublic,
        Boolean allowComment,
        Long categoryId,
        String categoryName,
        LocalDateTime updatedAt,

        String contentMarkdown,
        LessonLevel level,
        Integer estimateTimeInMinutes
) implements KnowledgeUpdatedEvent{}

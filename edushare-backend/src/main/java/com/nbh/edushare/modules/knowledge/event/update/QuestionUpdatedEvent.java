package com.nbh.edushare.modules.knowledge.event.update;

import com.nbh.edushare.modules.knowledge.enums.KnowledgeType;

import java.time.LocalDateTime;

public record QuestionUpdatedEvent(
        Long knowledgeId,
        KnowledgeType type,
        Long ownerId,
        String title,
        String abstractText,
        String thumbnailUrl,
        Boolean allowComment,
        Boolean isPublic,
        Long categoryId,
        String categoryName,
        LocalDateTime updatedAt,

        Boolean isResolved,
        Long acceptedAnswerId,
        String content
) implements KnowledgeUpdatedEvent{}

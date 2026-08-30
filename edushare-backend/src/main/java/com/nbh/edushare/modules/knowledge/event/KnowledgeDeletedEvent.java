package com.nbh.edushare.modules.knowledge.event;

public record KnowledgeDeletedEvent(
        Long knowledgeId,
        String type,
        Long ownerId,
        Long deletedBy
) {}

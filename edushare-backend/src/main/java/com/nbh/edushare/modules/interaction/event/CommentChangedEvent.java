package com.nbh.edushare.modules.interaction.event;

public record CommentChangedEvent(Long knowledgeId, int delta) {}
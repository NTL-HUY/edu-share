package com.nbh.edushare.modules.interaction.event;

public record VoteChangedEvent(Long knowledgeId, int delta) {}

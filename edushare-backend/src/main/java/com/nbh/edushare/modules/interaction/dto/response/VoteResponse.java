package com.nbh.edushare.modules.interaction.dto.response;

public record VoteResponse(
        Long knowledgeId,
        Short currentValue,
        long voteScore
) {}
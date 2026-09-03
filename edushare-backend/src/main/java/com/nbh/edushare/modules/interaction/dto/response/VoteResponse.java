package com.nbh.edushare.modules.interaction.dto.response;

public record VoteResponse(
        Long knowledgeId,
        Short currentValue, // null nếu vừa unvote
        long voteScore      // best-effort, đã cộng cả phần Redis chưa flush
) {}
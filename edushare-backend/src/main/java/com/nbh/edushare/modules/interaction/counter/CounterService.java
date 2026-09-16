package com.nbh.edushare.modules.interaction.counter;

import org.springframework.transaction.annotation.Transactional;

public interface CounterService {
    void incrView(long knowledgeId);
    void incrVote(long knowledgeId, int delta);
    void incrComment(long knowledgeId, int delta);

    CounterDelta getPendingDelta(long knowledgeId);
    void flushOne(long knowledgeId);
}
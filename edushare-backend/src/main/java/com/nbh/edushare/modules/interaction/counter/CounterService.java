package com.nbh.edushare.modules.interaction.counter;

import org.springframework.transaction.annotation.Transactional;

public interface CounterService {
    void incrView(long knowledgeId);
    void incrVote(long knowledgeId, int delta);
    void incrComment(long knowledgeId, int delta);

    /** Delta hiện có trong Redis, chưa flush xuống DB (0 nếu chưa có gì). */
    CounterDelta getPendingDelta(long knowledgeId);
    void flushOne(long knowledgeId);
}
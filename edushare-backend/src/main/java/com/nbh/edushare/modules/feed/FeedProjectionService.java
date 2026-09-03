package com.nbh.edushare.modules.feed;

import com.nbh.edushare.modules.feed.pojo.FeedItem;
import com.nbh.edushare.modules.knowledge.event.KnowledgeDeletedEvent;
import com.nbh.edushare.modules.knowledge.event.create.KnowledgeCreatedEvent;
import com.nbh.edushare.modules.knowledge.event.update.KnowledgeUpdatedEvent;

public interface FeedProjectionService {
    FeedItem processKnowledgeCreated(KnowledgeCreatedEvent event);
    FeedItem processKnowledgeUpdated(KnowledgeUpdatedEvent event);
    void processKnowledgeDeleted(KnowledgeDeletedEvent event);
    void fanOutToFollowers(long ownerId, long knowledgeId);
}
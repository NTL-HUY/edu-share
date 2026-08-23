package com.nbh.edushare.modules.feed;

import com.nbh.edushare.modules.feed.dto.request.FeedSearchInput;
import com.nbh.edushare.modules.feed.dto.response.FeedPage;
import com.nbh.edushare.modules.feed.dto.response.FeedSearchResult;
import com.nbh.edushare.modules.feed.pojo.FeedItem;
import com.nbh.edushare.modules.knowledge.event.create.KnowledgeCreatedEvent;
import com.nbh.edushare.modules.knowledge.event.update.KnowledgeUpdatedEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface FeedService {
    FeedItem processKnowledgeCreated(KnowledgeCreatedEvent event);
    void fanOutToFollowers(long ownerId, long knowledgeId);
    <T> Optional<T> findProjectedById(Long id, Class<T> type);
    FeedPage getFeed(Long userId, String cursorStr, int limit);
    FeedItem processKnowledgeUpdated(KnowledgeUpdatedEvent event);
    FeedSearchResult searchFeed(FeedSearchInput input, Pageable pageable);
    int adjustCounters(long id, int views,int votes, int comments);

    boolean existsByKnowledgeId(Long knowledgeId);
}

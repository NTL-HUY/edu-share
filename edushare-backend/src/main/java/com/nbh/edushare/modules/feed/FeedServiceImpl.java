package com.nbh.edushare.modules.feed;

import com.nbh.edushare.modules.feed.cache.FeedCacheService;
import com.nbh.edushare.modules.feed.dto.request.FeedSearchInput;
import com.nbh.edushare.modules.feed.dto.response.FeedPage;
import com.nbh.edushare.modules.feed.dto.response.FeedSearchResult;
import com.nbh.edushare.modules.feed.pojo.FeedItem;
import com.nbh.edushare.modules.feed.query.FeedQueryService;
import com.nbh.edushare.modules.feed.repository.FeedItemRepository;
import com.nbh.edushare.modules.feed.util.FeedCursor;
import com.nbh.edushare.modules.feed.util.FeedItemSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
class FeedServiceImpl implements FeedService {
    @Value("${app.feed.cache-ttl-seconds:30}")
    private long feedCacheTtlSeconds;

    @Value("${app.feed.discovery-cache-ttl-seconds:60}")
    private long discoveryCacheTtlSeconds;

    private final FeedItemRepository feedItemRepository;
    private final FeedQueryService feedQueryService;
    private final FeedCacheService feedCacheService;

    @Override
    public FeedSearchResult searchFeed(FeedSearchInput input, Pageable pageable) {
        Specification<FeedItem> spec = FeedItemSpecification.build(input);

        Page<FeedItem> result = feedItemRepository.findAll(spec, pageable);
        return FeedSearchResult.from(result);
    }

    @Override
    public int adjustCounters(long id, int views, int votes, int comments) {
        return feedItemRepository.adjustCounters(id, views, votes, comments);
    }

    @Override
    public FeedPage getFeed(Long userId, String cursorStr, int limit){
        if (userId == null) {
            return getDiscoveryFeed(cursorStr, limit);
        }

        String cacheKey = feedCacheService.buildFeedCacheKey(userId, cursorStr, limit);
        FeedPage cached = feedCacheService.getFeed(cacheKey);
        if (cached != null) {
            return cached;
        }

        FeedCursor cursor = FeedCursor.decode(cursorStr);
        FeedPage feedPage = feedQueryService.loadFeedByUserId(userId, cursor, limit);

        feedCacheService.putFeed(cacheKey, feedPage, feedCacheTtlSeconds);

        return feedPage;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByKnowledgeId(Long knowledgeId) {
        return feedItemRepository.existsByKnowledgeId(knowledgeId);
    }

    @Transactional(readOnly = true)
    @Override
    public <T> Optional<T> findProjectedById(Long id, Class<T> type){
        return feedItemRepository.findProjectedByKnowledgeIdAndDeletedAtIsNull(id, type);
    };

    private FeedPage getDiscoveryFeed(String cursorStr, int limit) {
        String cacheKey = feedCacheService.buildDiscoveryCacheKey(cursorStr, limit);
        FeedPage cached = feedCacheService.getFeed(cacheKey);
        if (cached != null) {
            return cached;
        }

        FeedCursor cursor = FeedCursor.decode(cursorStr);
        FeedPage feedPage = feedQueryService.loadDiscoveryFeed(cursor, limit);

        feedCacheService.putFeed(cacheKey, feedPage, discoveryCacheTtlSeconds);

        return feedPage;
    }
}

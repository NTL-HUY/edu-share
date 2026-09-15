package com.nbh.edushare.modules.feed.cache;

import com.nbh.edushare.modules.feed.dto.response.FeedPage;

public interface FeedCacheService {
    FeedPage getFeed(String cacheKey);

    void putFeed(String cacheKey, FeedPage feedPage, long ttlSeconds);

    String buildFeedCacheKey(Long userId, String cursor, int limit);

    String buildDiscoveryCacheKey(String cursorStr, int limit);
}

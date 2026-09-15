package com.nbh.edushare.modules.feed.query;

import com.nbh.edushare.modules.feed.dto.response.FeedPage;
import com.nbh.edushare.modules.feed.util.FeedCursor;
import org.springframework.transaction.annotation.Transactional;

public interface FeedQueryService {
    @Transactional(readOnly = true)
    FeedPage loadFeedByUserId(Long userId, FeedCursor cursor, int limit);

    @Transactional(readOnly = true)
    FeedPage loadDiscoveryFeed(FeedCursor cursor, int limit);
}

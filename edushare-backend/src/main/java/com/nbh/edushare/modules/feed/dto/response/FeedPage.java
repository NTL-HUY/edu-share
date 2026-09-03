package com.nbh.edushare.modules.feed.dto.response;

import com.nbh.edushare.modules.feed.pojo.FeedItem;

import java.util.List;

public record FeedPage(
        List<FeedItem> items,
        String nextCursor,
        boolean hasMore
) {}

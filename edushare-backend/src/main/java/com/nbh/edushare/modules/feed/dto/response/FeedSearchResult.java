package com.nbh.edushare.modules.feed.dto.response;

import com.nbh.edushare.modules.feed.pojo.FeedItem;
import org.springframework.data.domain.Page;

import java.util.List;

public record FeedSearchResult(
        List<FeedItem> items,
        long totalCount,
        int page,
        int totalPages
){
    public static FeedSearchResult from(Page<FeedItem> pageResult) {
        return new FeedSearchResult(
                pageResult.getContent(),
                pageResult.getTotalElements(),
                pageResult.getNumber(),
                pageResult.getTotalPages()
        );
    }
}
package com.nbh.edushare.modules.feed.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record FeedQueryInput(
        String cursor,

        @Min(value = 1, message = "Limit must be at least 1")
        @Max(value = 50, message = "Limit cannot exceed 50")
        Integer limit
) {
    public int getSafeLimit() {
        return (limit == null) ? 20 : limit;
    }
}
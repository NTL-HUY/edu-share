package com.nbh.edushare.modules.feed.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record FeedQueryInput(
        String cursor,

        @Min(value = 1, message = "Limit phải ít nhất từ 1")
        @Max(value = 50, message = "Limit không được quá 50")
        Integer limit
) {
    public int getSafeLimit() {
        return (limit == null) ? 20 : limit;
    }
}
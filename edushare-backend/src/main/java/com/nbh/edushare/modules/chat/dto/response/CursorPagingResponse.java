package com.nbh.edushare.modules.chat.dto.response;


import java.util.List;

public record CursorPagingResponse <T>(
        List<T> items,
        Long beforeId,
        boolean hasMore

) {
    public static <T> CursorPagingResponse<T> of(List<T> items, Long beforeId, boolean hasMore) {
        return new CursorPagingResponse<>(items, beforeId, hasMore);
    }

    public static <T> CursorPagingResponse<T> empty() {
        return new CursorPagingResponse<>(List.of(), null, false);
    }
}

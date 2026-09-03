package com.nbh.edushare.common.utils;

import java.util.List;

public record CursorPaging<T>(
        List<T> items,
        String nextCursor,
        boolean hasMore
) {
    public static <T> CursorPaging<T> of(List<T> items, String nextCursor, boolean hasMore) {
        return new CursorPaging<>(items, nextCursor, hasMore);
    }

    public static <T> CursorPaging<T> empty() {
        return new CursorPaging<>(List.of(), null, false);
    }
}

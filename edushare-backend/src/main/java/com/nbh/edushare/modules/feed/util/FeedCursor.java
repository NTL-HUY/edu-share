package com.nbh.edushare.modules.feed.util;

import com.nbh.edushare.common.exception.AppException;
import com.nbh.edushare.modules.feed.exception.FeedErrorCode;

import java.time.LocalDateTime;
import java.util.Base64;

public record FeedCursor(LocalDateTime createdAt, Long id) {

    public static FeedCursor decode(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        try {
            String raw = new String(Base64.getDecoder().decode(cursor));
            String[] parts = raw.split("_", 2);
            return new FeedCursor(LocalDateTime.parse(parts[0]), Long.parseLong(parts[1]));
        } catch (Exception e) {
            throw new AppException(FeedErrorCode.INVALID_CURSOR);
        }
    }

    public String encode() {
        String raw = createdAt.toString() + "_" + id;
        return Base64.getEncoder().encodeToString(raw.getBytes());
    }
}

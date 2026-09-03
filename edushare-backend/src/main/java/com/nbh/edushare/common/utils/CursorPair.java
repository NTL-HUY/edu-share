package com.nbh.edushare.common.utils;

import com.nbh.edushare.common.exception.AppException;

import java.time.LocalDateTime;
import java.util.Base64;

public record CursorPair(LocalDateTime createdAt, Long id) {
    public static CursorPair decode(String cursor) {
        if (cursor == null || cursor.isBlank()) return null;
        try {
            String raw = new String(Base64.getDecoder().decode(cursor));
            String[] parts = raw.split("_", 2);
            return new CursorPair(LocalDateTime.parse(parts[0]), Long.parseLong(parts[1]));
        } catch (Exception e) {
            throw new AppException("Cursor không hợp lệ");
        }
    }

    public String encode() {
        return Base64.getEncoder().encodeToString((createdAt + "_" + id).getBytes());
    }
}

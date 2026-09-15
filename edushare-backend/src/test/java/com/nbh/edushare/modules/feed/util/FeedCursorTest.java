package com.nbh.edushare.modules.feed.util;

import com.nbh.edushare.common.exception.AppException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeedCursorTest {

    @Test
    void encodeDecode_roundTrip() {
        LocalDateTime t = LocalDateTime.of(2026, 5, 6, 7, 8, 9);
        FeedCursor cursor = new FeedCursor(t, 42L);

        assertEquals(cursor, FeedCursor.decode(cursor.encode()));
    }

    @Test
    void decode_null_returnsNull() {
        assertNull(FeedCursor.decode(null));
    }

    @Test
    void decode_blank_returnsNull() {
        assertNull(FeedCursor.decode("   "));
    }

    @Test
    void decode_invalidBase64_throwsAppException() {
        assertThrows(AppException.class, () -> FeedCursor.decode("@@not-base64@@"));
    }

    @Test
    void decode_malformedContent_throwsAppException() {
        String encoded = Base64.getEncoder().encodeToString("garbage-here".getBytes());
        assertThrows(AppException.class, () -> FeedCursor.decode(encoded));
    }

    @Test
    void decode_nonNumericId_throwsAppException() {
        String encoded = Base64.getEncoder().encodeToString("2026-01-01T10:00_x".getBytes());
        assertThrows(AppException.class, () -> FeedCursor.decode(encoded));
    }
}
package com.nbh.edushare.modules.interaction.counter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisCounterConstants {

    // Keys
    public static final String DIRTY_SET_KEY = "kn:dirty";
    public static final String LOCK_KEY = "kn:flush:lock";
    private static final String COUNTER_KEY_PREFIX = "kn:counters:";

    // Fields
    public static final String FIELD_VIEWS = "views";
    public static final String FIELD_VOTES = "votes";
    public static final String FIELD_COMMENTS = "comments";

    // Helper Method
    public static String getFeedCounterKey(long knowledgeId) {
        return COUNTER_KEY_PREFIX + knowledgeId;
    }

    public static long parseLong(Object value) {
        if (value == null) return 0L;
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
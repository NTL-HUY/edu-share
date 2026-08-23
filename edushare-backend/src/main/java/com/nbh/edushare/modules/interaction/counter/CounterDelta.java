package com.nbh.edushare.modules.interaction.counter;

public record CounterDelta(
        long views,
        long votes,
        long comments
) {
    public static final CounterDelta EMPTY = new CounterDelta(0, 0, 0);
}

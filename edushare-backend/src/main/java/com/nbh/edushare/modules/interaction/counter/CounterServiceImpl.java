package com.nbh.edushare.modules.interaction.counter;

import com.nbh.edushare.modules.feed.FeedService;
import com.nbh.edushare.modules.knowledge.KnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CounterServiceImpl implements CounterService {

    // QUAN TRỌNG: dùng StringRedisTemplate (chuỗi thô), KHÔNG dùng
    // RedisTemplate<String,Object> đang cấu hình JSON serializer trong RedisConfig,
    // vì HINCRBY yêu cầu value là số dạng string thuần, không phải JSON.
    private final StringRedisTemplate redis;
    private final FeedService feedService;
    private final KnowledgeService knowledgeService;


    private String counterKey(long knowledgeId) {
        return RedisCounterConstants.getFeedCounterKey(knowledgeId);
    }

    @Override
    public void incrView(long knowledgeId) {
        incr(knowledgeId, RedisCounterConstants.FIELD_VIEWS, 1);
    }

    @Override
    public void incrVote(long knowledgeId, int delta) {
        if (delta == 0) return;
        incr(knowledgeId, RedisCounterConstants.FIELD_VOTES, delta);
    }

    @Override
    public void incrComment(long knowledgeId, int delta) {
        if (delta == 0) return;
        incr(knowledgeId, RedisCounterConstants.FIELD_COMMENTS, delta);
    }

    private void incr(long knowledgeId, String field, long delta) {
        String key = counterKey(knowledgeId);
        redis.opsForHash().increment(key, field, delta);
        redis.opsForSet().add(RedisCounterConstants.DIRTY_SET_KEY, String.valueOf(knowledgeId));
    }

    @Override
    public CounterDelta getPendingDelta(long knowledgeId) {
        Map<Object, Object> h = redis.opsForHash().entries(counterKey(knowledgeId));
        if (h.isEmpty()) return CounterDelta.EMPTY;
        return new CounterDelta(
                parse(h.get(RedisCounterConstants.FIELD_VIEWS)),
                parse(h.get(RedisCounterConstants.FIELD_VOTES)),
                parse(h.get(RedisCounterConstants.FIELD_COMMENTS))
        );
    }

    @Transactional
    @Override
    public void flushOne(long knowledgeId) {
        String key = RedisCounterConstants.getFeedCounterKey(knowledgeId);
        var snapshot = redis.opsForHash().entries(key);
        if (!snapshot.isEmpty()) {
            long views = parse(snapshot.get(RedisCounterConstants.FIELD_VIEWS));
            long votes = parse(snapshot.get(RedisCounterConstants.FIELD_VOTES));
            long comments = parse(snapshot.get(RedisCounterConstants.FIELD_COMMENTS));
            if (views != 0 || votes != 0 || comments != 0) {// 1. Cộng dồn vào bảng gốc knowledge
                knowledgeService.adjustCounters(knowledgeId, (int) views, (int) votes, (int) comments);// 2. Đồng bộ sang feed_item (bảng đọc, denormalized)
                feedService.adjustCounters(knowledgeId, (int) views, (int) votes, (int) comments);// 3. Trừ lại đúng phần vừa flush -> KHÔNG set = 0, để không mất write chen giữa
                redis.opsForHash().increment(key, RedisCounterConstants.FIELD_VIEWS, -views);
                redis.opsForHash().increment(key, RedisCounterConstants.FIELD_VOTES, -votes);
                redis.opsForHash().increment(key, RedisCounterConstants.FIELD_COMMENTS, -comments);
            }
        }

    }

    private long parse(Object v) {
        return RedisCounterConstants.parseLong(v);
    }
}
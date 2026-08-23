package com.nbh.edushare.modules.interaction.counter;

import com.nbh.edushare.modules.feed.FeedService;
import com.nbh.edushare.modules.knowledge.KnowledgeService;
import com.nbh.edushare.modules.knowledge.repository.KnowledgeRepository;
import com.nbh.edushare.modules.feed.repository.FeedItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CounterFlushScheduler {

    private final StringRedisTemplate redis;
    private final CounterService counterService;

    private static final String LOCK_KEY = "kn:flush:lock";
    private final String instanceId = UUID.randomUUID().toString();

    @Scheduled(fixedDelayString = "${app.counter.flush.fixed-delay-ms:5000}")
    public void flushDirtyCounters() {
        // Chống nhiều instance app cùng flush trùng nhau
        Boolean locked = redis.opsForValue()
                .setIfAbsent(LOCK_KEY, instanceId, Duration.ofSeconds(10));
        if (locked == null || !locked) return;

        try {
            Set<String> dirtyIds = redis.opsForSet().members(RedisCounterConstants.DIRTY_SET_KEY);
            if (dirtyIds == null || dirtyIds.isEmpty()) return;

            for (String idStr : dirtyIds) {
                long knowledgeId = Long.parseLong(idStr);
                try {
                    counterService.flushOne(knowledgeId);
                    redis.opsForSet().remove(RedisCounterConstants.DIRTY_SET_KEY, idStr);
                } catch (Exception e) {
                    log.error("Flush counter thất bại cho knowledge_id={}", knowledgeId, e);
                    // không remove khỏi dirty set -> lần sau retry lại
                }
            }
        } finally {
            redis.delete(LOCK_KEY);
        }
    }



    private long parse(Object v) {
        return RedisCounterConstants.parseLong(v);
    }
}
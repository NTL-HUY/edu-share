package com.nbh.edushare.modules.feed;

import com.nbh.edushare.modules.feed.dto.request.FeedSearchInput;
import com.nbh.edushare.modules.feed.dto.response.FeedPage;
import com.nbh.edushare.modules.feed.dto.response.FeedSearchResult;
import com.nbh.edushare.modules.feed.pojo.FeedItem;
import com.nbh.edushare.modules.feed.pojo.UserFeed;
import com.nbh.edushare.modules.feed.repository.FeedItemRepository;
import com.nbh.edushare.modules.feed.repository.UserFeedRepository;
import com.nbh.edushare.modules.feed.util.FeedCursor;
import com.nbh.edushare.modules.feed.util.FeedItemSpecification;
import com.nbh.edushare.modules.knowledge.event.create.KnowledgeCreatedEvent;
import com.nbh.edushare.modules.knowledge.event.update.KnowledgeUpdatedEvent;
import com.nbh.edushare.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class FeedServiceImpl implements FeedService {
    @Value("${app.feed.cache-ttl-seconds:30}")
    private long feedCacheTtlSeconds;

    @Value("${app.feed.discovery-cache-ttl-seconds:60}")
    private long discoveryCacheTtlSeconds;

//    private final FeedMapper feedMapper;
    private final FeedItemRepository  feedItemRepository;
    private final UserFeedRepository userFeedRepository;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public FeedSearchResult searchFeed(FeedSearchInput input, Pageable pageable) {
        Specification<FeedItem> spec = FeedItemSpecification.build(input);

        Page<FeedItem> result = feedItemRepository.findAll(spec, pageable);
        return FeedSearchResult.from(result);
    }

    @Override
    public int adjustCounters(long id, int views, int votes, int comments) {

        return feedItemRepository.adjustCounters(id, views, votes, comments);
    }



    @Transactional(readOnly = true)
    @Override
    public <T> Optional<T> findProjectedById(Long id, Class<T> type){
        return feedItemRepository.findProjectedByKnowledgeIdAndDeletedAtIsNull(id, type);
    };

    @Transactional(readOnly = true)
    @Override
    public FeedPage getFeed(Long userId, String cursorStr, int limit){
        if (userId == null) {
            return getDiscoveryFeed(cursorStr, limit);
        }

        String cacheKey = buildFeedCacheKey(userId, cursorStr, limit);
        FeedPage cached = (FeedPage) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        FeedCursor cursor = FeedCursor.decode(cursorStr);
        int effectiveLimit = limit + 1;
        Pageable pageable = PageRequest.of(0, effectiveLimit);

        Map<Long, FeedItem> pool = new LinkedHashMap<>();

        // 1. Pushed (đã lọc public/owner ở repo)
        List<FeedItem> pushed = (cursor == null)
                ? userFeedRepository.findPushedFeedFirstPage(userId, pageable)
                : userFeedRepository.findPushedFeed(userId, cursor.createdAt(), cursor.id(), pageable);
        putAll(pool, pushed);

        // 2. Famous - chỉ public
        List<Long> famousIds = userService.findFamousFolloweeIds(userId);
        if (!famousIds.isEmpty()) {
            List<FeedItem> kolFeed = (cursor == null)
                    ? feedItemRepository.findLatestPublicByOwners(famousIds, pageable)
                    : feedItemRepository.findOlderPublicByOwners(famousIds, cursor.createdAt(), cursor.id(), pageable);
            putAll(pool, kolFeed);
        }

        // 3. Fallback followee thường - chỉ public, chỉ fetch khi vẫn còn thiếu
        if (pool.size() < effectiveLimit) {
            List<Long> normalIds = userService.findNormalFolloweeIds(userId);
            if (!normalIds.isEmpty()) {
                Pageable fallbackPageable = PageRequest.of(0, effectiveLimit - pool.size());
                List<FeedItem> fallback = (cursor == null)
                        ? feedItemRepository.findLatestPublicByOwners(normalIds, fallbackPageable)
                        : feedItemRepository.findOlderPublicByOwners(normalIds, cursor.createdAt(), cursor.id(), fallbackPageable);
                putAll(pool, fallback);
            }
        }

        // 4. Discovery - chỉ public, chỉ fetch khi vẫn còn thiếu
        if (pool.size() < effectiveLimit) {
            List<Long> excludeIds = pool.isEmpty() ? List.of(-1L) : new ArrayList<>(pool.keySet());
            Pageable discoveryPageable = PageRequest.of(0, effectiveLimit - pool.size());
            List<FeedItem> discovery = (cursor == null)
                    ? feedItemRepository.findLatestPublicDiscovery(excludeIds, discoveryPageable)
                    : feedItemRepository.findOlderPublicDiscovery(excludeIds, cursor.createdAt(), cursor.id(), discoveryPageable);
            putAll(pool, discovery);
        }

        List<FeedItem> sorted = pool.values().stream()
                .sorted(Comparator.comparing(FeedItem::getSourceCreatedAt)
                        .thenComparing(FeedItem::getKnowledgeId)
                        .reversed())
                .toList();

        boolean hasMore = sorted.size() > limit;
        List<FeedItem> finalList = hasMore ? sorted.subList(0, limit) : sorted;

        String nextCursor = finalList.isEmpty() ? null
                : new FeedCursor(
                finalList.getLast().getSourceCreatedAt(),
                finalList.getLast().getKnowledgeId()
        ).encode();

        FeedPage feedPage = new FeedPage(finalList, nextCursor, hasMore);

        redisTemplate.opsForValue().set(cacheKey, feedPage, Duration.ofSeconds(feedCacheTtlSeconds));

        return feedPage;
    }

    private String buildFeedCacheKey(Long userId, String cursor, int limit) {
        return String.format("feed:user:%d:cursor:%s:limit:%d", userId, cursor != null ? cursor : "null", limit);
    }

    private String buildDiscoveryCacheKey(String cursorStr, int limit) {
        return "feed:discovery:%s:%d".formatted(cursorStr == null ? "first" : cursorStr, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByKnowledgeId(Long knowledgeId) {
        return feedItemRepository.existsByKnowledgeId(knowledgeId);
    }

    private FeedPage getDiscoveryFeed(String cursorStr, int limit) {
        String cacheKey = buildDiscoveryCacheKey(cursorStr, limit);
        FeedPage cached = (FeedPage) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        FeedCursor cursor = FeedCursor.decode(cursorStr);
        Pageable pageable = PageRequest.of(0, limit + 1);

        List<FeedItem> discovery = (cursor == null)
                ? feedItemRepository.findLatestPublicDiscovery(List.of(-1L), pageable)
                : feedItemRepository.findOlderPublicDiscovery(List.of(-1L), cursor.createdAt(), cursor.id(), pageable);

        boolean hasMore = discovery.size() > limit;
        List<FeedItem> finalList = hasMore ? discovery.subList(0, limit) : discovery;

        String nextCursor = finalList.isEmpty() ? null
                : new FeedCursor(
                finalList.getLast().getSourceCreatedAt(),
                finalList.getLast().getKnowledgeId()
        ).encode();

        FeedPage feedPage = new FeedPage(finalList, nextCursor, hasMore);

        redisTemplate.opsForValue().set(cacheKey, feedPage, Duration.ofSeconds(discoveryCacheTtlSeconds));

        return feedPage;
    }

    private void putAll(Map<Long, FeedItem> pool, List<FeedItem> items) {
        for (FeedItem item : items) {
            pool.putIfAbsent(item.getKnowledgeId(), item);
        }
    }
}

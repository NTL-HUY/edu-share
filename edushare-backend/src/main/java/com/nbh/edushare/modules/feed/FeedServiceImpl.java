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

    private final FeedMapper feedMapper;
    private final FeedItemRepository  feedItemRepository;
    private final UserFeedRepository userFeedRepository;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;


    @Override
    @Transactional
    public FeedItem processKnowledgeCreated(KnowledgeCreatedEvent event) {
        FeedItem feedItem = feedMapper.toEntity(event);
        return feedItemRepository.save(feedItem);
    }

    @Override
    @Transactional
    public FeedItem processKnowledgeUpdated(KnowledgeUpdatedEvent event) {
        FeedItem feedItem = feedItemRepository.findById(event.knowledgeId())
                .orElseThrow(() -> new IllegalStateException(
                        "FeedItem not found for knowledgeId=" + event.knowledgeId()));

        feedMapper.updateEntity(feedItem, event);
        return feedItemRepository.save(feedItem);
    }

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

    @Transactional
    @Override
    public void fanOutToFollowers(long ownerId, long knowledgeId) {
        List<Long> followerIds = userService.getFollowerIds(ownerId);

        LocalDateTime now = LocalDateTime.now();

        List<UserFeed> userFeeds = followerIds.stream()
                .map(followerId -> new UserFeed(followerId, knowledgeId, now))
                .toList();

        userFeedRepository.saveAll(userFeeds);
    }

    @Transactional(readOnly = true)
    @Override
    public <T> Optional<T> findProjectedById(Long id, Class<T> type){
        return feedItemRepository.findProjectedByKnowledgeId(id, type);
    };

    private String buildFeedCacheKey(Long userId, String cursorStr, int limit) {
        return "feed:user:%d:%s:%d".formatted(userId, cursorStr == null ? "first" : cursorStr, limit);
    }

    @Transactional(readOnly = true)
    @Override
    public FeedPage getFeed(Long userId, String cursorStr, int limit){
        String cacheKey = buildFeedCacheKey(userId, cursorStr, limit);

        // 1. Check cache trước
        FeedPage cached = (FeedPage) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        FeedCursor cursor = FeedCursor.decode(cursorStr);
        Pageable pageable = PageRequest.of(0, limit);

        // pushed
        List<FeedItem> pushed = (cursor == null)
                ? userFeedRepository.findPushedFeedFirstPage(userId, pageable)
                : userFeedRepository.findPushedFeed(userId, cursor.createdAt(), cursor.id(), pageable);

        // Famous
        List<Long> famousIds = userService.findFamousFolloweeIds(userId);
        List<FeedItem> kolFeed = famousIds.isEmpty() ? List.of()
                : (cursor == null
                   ? feedItemRepository.findLatestByOwners(famousIds, pageable)
                   : feedItemRepository.findOlderByOwners(famousIds, cursor.createdAt(), cursor.id(), pageable));

        Map<Long, FeedItem> primaryFeedMap = new LinkedHashMap<>();
        for (FeedItem item : pushed) {
            primaryFeedMap.put(item.getKnowledgeId(), item);
        }
        for (FeedItem item : kolFeed) {
            primaryFeedMap.putIfAbsent(item.getKnowledgeId(), item);
        }
        List<FeedItem> result = primaryFeedMap.values().stream()
                .sorted(Comparator.comparing(FeedItem::getSourceCreatedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        // Nguồn 3: fallback followee
        int missing = limit - result.size();
        if (missing > 0) {
            List<Long> normalIds = userService.findNormalFolloweeIds(userId);
            if (!normalIds.isEmpty()) {
                Pageable fallbackPageable = PageRequest.of(0, missing);
                List<FeedItem> fallback = (cursor == null)
                        ? feedItemRepository.findLatestByOwners(normalIds, fallbackPageable)
                        : feedItemRepository.findOlderByOwners(normalIds, cursor.createdAt(), cursor.id(), fallbackPageable);
                result.addAll(fallback);
            }
        }

        //  discovery
        missing = limit - result.size();
        if (missing > 0) {
            List<Long> excludeIds = result.stream().map(FeedItem::getKnowledgeId).toList();
            List<Long> safeExclude = excludeIds.isEmpty() ? List.of(-1L) : excludeIds;

            Pageable discoveryPageable = PageRequest.of(0, missing);
            List<FeedItem> discovery = (cursor == null)
                    ? feedItemRepository.findLastestDiscovery(safeExclude, discoveryPageable)
                    : feedItemRepository.findOlderDiscovery(safeExclude, cursor.createdAt(), cursor.id(), discoveryPageable);
            result.addAll(discovery);
        }

        List<FeedItem> finalList = result.stream()
                .sorted(Comparator.comparing(FeedItem::getSourceCreatedAt).reversed())
                .limit(limit)
                .toList();

        boolean hasMore = finalList.size() == limit;
        String nextCursor = finalList.isEmpty() ? null
                : new FeedCursor(
                finalList.getLast().getSourceCreatedAt(),
                finalList.getLast().getKnowledgeId()
        ).encode();

        FeedPage feedPage = new FeedPage(finalList, nextCursor, hasMore);

        redisTemplate.opsForValue().set(cacheKey, feedPage, Duration.ofSeconds(feedCacheTtlSeconds));

        return feedPage;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByKnowledgeId(Long knowledgeId) {
        return feedItemRepository.existsByKnowledgeId(knowledgeId);
    }
}

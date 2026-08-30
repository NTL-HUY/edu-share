package com.nbh.edushare.modules.feed;

import com.nbh.edushare.modules.feed.pojo.FeedItem;
import com.nbh.edushare.modules.feed.pojo.UserFeed;
import com.nbh.edushare.modules.feed.repository.FeedItemRepository;
import com.nbh.edushare.modules.feed.repository.UserFeedRepository;
import com.nbh.edushare.modules.knowledge.event.KnowledgeDeletedEvent;
import com.nbh.edushare.modules.knowledge.event.create.KnowledgeCreatedEvent;
import com.nbh.edushare.modules.knowledge.event.update.KnowledgeUpdatedEvent;
import com.nbh.edushare.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedProjectionServiceImpl implements FeedProjectionService {
    private final FeedMapper feedMapper;
    private final FeedItemRepository feedItemRepository;
    private final UserService userService;
    private final UserFeedRepository userFeedRepository;


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
    @Transactional
    public void processKnowledgeDeleted(KnowledgeDeletedEvent event) {
        int updated = feedItemRepository.markDeleted(event.knowledgeId(), LocalDateTime.now());

        if (updated == 0) {
            log.info("Skip processKnowledgeDeleted: feedItem {} đã bị xóa hoặc không tìm thấy (có thể message đã gửi trùng lặp)",
                    event.knowledgeId());
            return;
        }

        long removed = userFeedRepository.deleteByFeedItemId(event.knowledgeId());
        log.info("Đã xóa {} dòng user_feed cho knowledgeId={}", removed, event.knowledgeId());
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
}

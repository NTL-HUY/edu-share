package com.nbh.edushare.modules.feed.listener;

import com.nbh.edushare.modules.feed.FeedService;
import com.nbh.edushare.modules.feed.pojo.FeedItem;
import com.nbh.edushare.modules.knowledge.config.KnowledgeKafkaConfig;
import com.nbh.edushare.modules.knowledge.event.create.KnowledgeCreatedEvent;
import com.nbh.edushare.modules.knowledge.event.update.KnowledgeUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KnowledgeFeedListener {
//    private static final int KOL_THRESHOLD = 5000; // tạm định nghĩa, chỉnh sau
    private final FeedService feedService;

    @KafkaListener(topics = KnowledgeKafkaConfig.KNOWLEDGE_CREATED_TOPIC, groupId = "feed-service")
    public void onKnowledgeCreated(KnowledgeCreatedEvent event) {
        log.info("Received knowledge-created event: knowledgeId={}, ownerId={}",
                event.getKnowledgeId(), event.getOwnerId());

        FeedItem feedItem = feedService.processKnowledgeCreated(event);
        log.info("Saved FeedItem copy: knowledgeId={}", feedItem.getKnowledgeId());

        if (event.getIsFamous()) {
            log.info("Owner {} is a famous/KOL user. Skipping fan-out to prevent database bloat.", event.getOwnerId());
            return;
        }

        feedService.fanOutToFollowers(event.getOwnerId(), feedItem.getKnowledgeId());
        log.info("Fan-out completed: ownerId={}, knowledgeId={}",
                event.getOwnerId(), feedItem.getKnowledgeId());
    }

    @KafkaListener(topics = KnowledgeKafkaConfig.KNOWLEDGE_UPDATED_TOPIC, groupId = "feed-service")
    public void onKnowledgeUpdated(KnowledgeUpdatedEvent event) {
        log.info("Received knowledge-updated event: knowledgeId={}, ownerId={}",
                event.knowledgeId(), event.ownerId());

        FeedItem feedItem = feedService.processKnowledgeUpdated(event);
        log.info("Synced FeedItem copy: knowledgeId={}", feedItem.getKnowledgeId());
    }
}

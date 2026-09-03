package com.nbh.edushare.modules.feed.listener;

import com.nbh.edushare.modules.feed.FeedProjectionService;
import com.nbh.edushare.modules.feed.FeedService;
import com.nbh.edushare.modules.feed.pojo.FeedItem;
import com.nbh.edushare.modules.knowledge.config.KnowledgeKafkaConfig;
import com.nbh.edushare.modules.knowledge.event.KnowledgeDeletedEvent;
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
    private final FeedProjectionService projectionService;

    @KafkaListener(topics = KnowledgeKafkaConfig.KNOWLEDGE_CREATED_TOPIC, groupId = "feed-service")
    public void onKnowledgeCreated(KnowledgeCreatedEvent event) {
        log.info("Đã nhận knowledge-created event: knowledgeId={}, ownerId={}",
                event.getKnowledgeId(), event.getOwnerId());

        FeedItem feedItem = projectionService.processKnowledgeCreated(event);
        log.info("Đã lưu FeedItem copy: knowledgeId={}", feedItem.getKnowledgeId());

        if (event.getIsFamous()) {
            log.info("Owner {} is a famous/KOL user. Skipping fan-out to prevent database bloat.", event.getOwnerId());
            return;
        }

        projectionService.fanOutToFollowers(event.getOwnerId(), feedItem.getKnowledgeId());
        log.info("Fan-out hoàn thành: ownerId={}, knowledgeId={}",
                event.getOwnerId(), feedItem.getKnowledgeId());
    }

    @KafkaListener(topics = KnowledgeKafkaConfig.KNOWLEDGE_UPDATED_TOPIC, groupId = "feed-service")
    public void onKnowledgeUpdated(KnowledgeUpdatedEvent event) {
        log.info("Đã nhận knowledge-updated event: knowledgeId={}, ownerId={}",
                event.knowledgeId(), event.ownerId());

        FeedItem feedItem = projectionService.processKnowledgeUpdated(event);
        log.info("Đã Đồng bồ FeedItem copy: knowledgeId={}", feedItem.getKnowledgeId());
    }

    @KafkaListener(topics = KnowledgeKafkaConfig.KNOWLEDGE_DELETED_TOPIC, groupId = "feed-service")
    public void onKnowledgeDeleted(KnowledgeDeletedEvent event) {
        log.info("Đã nhận knowledge-deleted event: knowledgeId={}, ownerId={}",
                event.knowledgeId(), event.ownerId());

        projectionService.processKnowledgeDeleted(event);
        log.info("Đã xóa FeedItem copy và UserFeed: knowledgeId={}", event.knowledgeId());
    }
}

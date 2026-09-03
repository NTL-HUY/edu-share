package com.nbh.edushare.modules.knowledge.publisher;

import com.nbh.edushare.modules.knowledge.config.KnowledgeKafkaConfig;
import com.nbh.edushare.modules.knowledge.event.KnowledgeDeletedEvent;
import com.nbh.edushare.modules.knowledge.event.create.KnowledgeCreatedEvent;
import com.nbh.edushare.modules.knowledge.event.update.KnowledgeUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KnowledgeKafkaPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Async("kafkaPublisherExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onKnowledgeCreated(KnowledgeCreatedEvent event) {
        kafkaTemplate.send(
                KnowledgeKafkaConfig.KNOWLEDGE_CREATED_TOPIC,
                event.getKnowledgeId().toString(),
                event
        ).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Gửi Kafka thất bại cho knowledgeId={}", event.getKnowledgeId(), ex);
            } else {
                log.info("Đã gửi Kafka knowledge-created: knowledgeId={}", event.getKnowledgeId());
            }
        });
    }

    @Async("kafkaPublisherExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onKnowledgeUpdated(KnowledgeUpdatedEvent event) {
        kafkaTemplate.send(
                KnowledgeKafkaConfig.KNOWLEDGE_UPDATED_TOPIC,
                event.knowledgeId().toString(),
                event
        ).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Gửi Kafka thất bại cho knowledgeId={}", event.knowledgeId(), ex);
            } else {
                log.info("Đã gửi Kafka knowledge-updated: knowledgeId={}", event.knowledgeId());
            }
        });
    }

    @Async("kafkaPublisherExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onKnowledgeDeleted(KnowledgeDeletedEvent event) {
        kafkaTemplate.send(
                KnowledgeKafkaConfig.KNOWLEDGE_DELETED_TOPIC,
                event.knowledgeId().toString(),
                event
        ).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Gửi Kafka thất bại cho knowledgeId={}", event.knowledgeId(), ex);
            } else {
                log.info("Đã gửi Kafka knowledge-deleted: knowledgeId={}", event.knowledgeId());
            }
        });
    }
}

package com.nbh.edushare.modules.knowledge.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KnowledgeKafkaConfig {

    public static final String KNOWLEDGE_CREATED_TOPIC = "knowledge-created";
    public static final String KNOWLEDGE_UPDATED_TOPIC = "knowledge-updated";
    public static final String KNOWLEDGE_DELETED_TOPIC = "knowledge-deleted";


    @Bean
    public NewTopic knowledgeCreatedTopic() {
        return TopicBuilder.name(KNOWLEDGE_CREATED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}

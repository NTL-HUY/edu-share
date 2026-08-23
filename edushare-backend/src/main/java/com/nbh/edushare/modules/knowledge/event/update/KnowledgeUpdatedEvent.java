package com.nbh.edushare.modules.knowledge.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nbh.edushare.modules.knowledge.enums.KnowledgeType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LessonUpdatedEvent.class, name = "LESSON_UPDATED"),
        @JsonSubTypes.Type(value = QuestionUpdatedEvent.class, name = "QUESTION_UPDATED")
})
public sealed interface KnowledgeUpdatedEvent
        permits LessonUpdatedEvent, QuestionUpdatedEvent {

    Long knowledgeId();
    KnowledgeType type();
    Long ownerId();
    String title();
    String abstractText();
    String thumbnailUrl();
    Boolean isPublic();
    Long categoryId();
    String categoryName();
    LocalDateTime updatedAt();
}
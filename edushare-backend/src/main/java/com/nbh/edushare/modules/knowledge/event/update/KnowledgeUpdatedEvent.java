package com.nbh.edushare.modules.knowledge.event.update;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nbh.edushare.modules.knowledge.enums.KnowledgeType;

import java.time.LocalDateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LessonUpdatedEvent.class, name = "LESSON_UPDATED"),
        @JsonSubTypes.Type(value = QuestionUpdatedEvent.class, name = "QUESTION_UPDATED")
})
public sealed interface KnowledgeUpdatedEvent
        permits LessonUpdatedEvent, QuestionUpdatedEvent {

    Long knowledgeId();
    String title();
    String abstractText();
    String thumbnailUrl();
    Boolean allowComment();
    Boolean isPublic();
    LocalDateTime updatedAt();


    Long categoryId();
    String categoryName();

    KnowledgeType type();
    Long ownerId();
}
package com.nbh.edushare.modules.knowledge.event.create;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nbh.edushare.modules.knowledge.enums.KnowledgeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LessonCreatedEvent.class, name = "LESSON_CREATED"),
        @JsonSubTypes.Type(value = QuestionCreatedEvent.class, name = "QUESTION_CREATED")
})
public abstract class KnowledgeCreatedEvent {

    private Long knowledgeId;
    private KnowledgeType type;
    private Long ownerId;
    private String ownerName;
    private String ownerAvatarUrl;
    private Boolean isFamous;
    private String title;
    private String abstractText;
    private String thumbnailUrl;
    private Boolean isPublic;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
}

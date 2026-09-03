package com.nbh.edushare.modules.knowledge.event.create;

import com.nbh.edushare.modules.knowledge.enums.KnowledgeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCreatedEvent extends KnowledgeCreatedEvent {

    private Boolean isResolved;
    private Long acceptedAnswerId;
    private String content;
}
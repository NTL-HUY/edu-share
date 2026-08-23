package com.nbh.edushare.modules.knowledge.event.create;

import com.nbh.edushare.modules.knowledge.enums.KnowledgeType;
import com.nbh.edushare.modules.knowledge.enums.LessonLevel;
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
public class LessonCreatedEvent extends KnowledgeCreatedEvent {

    private LessonLevel level;
    private Integer estimateTimeInMinutes;
    private String contentMarkdown;
}
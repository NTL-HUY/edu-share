package com.nbh.edushare.modules.knowledge.dto.response;
import com.nbh.edushare.modules.knowledge.enums.LessonLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDetailResponse extends KnowledgeDetailResponse {
    private String contentMarkdown;
    private LessonLevel level;
    private Integer estimateTimeInMinutes;
}

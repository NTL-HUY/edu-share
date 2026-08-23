package com.nbh.edushare.modules.knowledge.dto.response;

import com.nbh.edushare.modules.knowledge.enums.KnowledgeType;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;
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
public class QuestionDetailResponse extends KnowledgeDetailResponse {
    private String content;
    private boolean isResolved;
    private Long acceptedAnswerId;
}
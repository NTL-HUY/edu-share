package com.nbh.edushare.modules.knowledge.dto.response;

import com.nbh.edushare.modules.knowledge.enums.KnowledgeType;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class KnowledgeDetailResponse {
    private Long id;
    private KnowledgeType type;
    private String title;
    private String abstractText;
    private String thumbnailUrl;
    private boolean isPublic;
    private boolean allowComment;
    private CategoryResponse category;
    private UserSimpleResponse owner;
    private LocalDateTime createdAt;
    private Integer viewsCount;
    private Integer voteScore;
    private Integer commentCount;
}

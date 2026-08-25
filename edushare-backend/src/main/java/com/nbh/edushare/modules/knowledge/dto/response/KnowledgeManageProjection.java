package com.nbh.edushare.modules.knowledge.dto.response;

import java.time.LocalDateTime;

public interface KnowledgeManageProjection {
    Long getId();
    String getType();
    String getTitle();
    String getAbstractText();
    String getThumbnailUrl();
    Boolean getIsPublic();
    Integer getViewsCount();
    Integer getVoteScore();
    Integer getCommentCount();
    LocalDateTime getCreatedAt();
}

package com.nbh.edushare.modules.feed.dto.response;

public interface FeedCountProjection {

    Integer getViewsCount();

    Integer getVoteScore();

    Integer getCommentCount();
}

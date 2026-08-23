package com.nbh.edushare.modules.feed.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionFeedMeta {
    private Boolean isResolved;
    private Long acceptedAnswerId;
    private String content;
}
package com.nbh.edushare.modules.feed.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonFeedMeta {
    private String level;
    private Integer estimateTimeInMinutes;
    private String contentMarkdown;
}
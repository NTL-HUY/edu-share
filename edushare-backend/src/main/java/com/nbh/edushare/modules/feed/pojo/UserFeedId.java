package com.nbh.edushare.modules.feed.pojo;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserFeedId implements Serializable {
    private Long userId;
    private Long feedItemId;
}

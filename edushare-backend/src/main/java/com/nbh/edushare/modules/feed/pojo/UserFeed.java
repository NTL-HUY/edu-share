package com.nbh.edushare.modules.feed.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_feed")
@IdClass(UserFeedId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFeed {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "feed_item_id")
    private Long feedItemId;

    @Column(name = "fanned_out_at", nullable = false)
    private LocalDateTime fannedOutAt;

}
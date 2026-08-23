package com.nbh.edushare.modules.interaction.pojo;

import com.nbh.edushare.common.model.BaseModel;
import com.nbh.edushare.common.model.SoftDeleteModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends SoftDeleteModel {

    @Column(name = "knowledge_id", nullable = false)
    private Long knowledgeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "user_avatar_url", length = 255)
    private String userAvatarUrl;

    @Column(name = "root_comment_id")
    private Long rootCommentId;

    @Column(name = "reply_to_comment_id")
    private Long replyToCommentId;

    @Column(name = "reply_to_user_name", length = 100)
    private String replyToUserName;

    @Column(name = "reply_count", nullable = false)
    private Integer replyCount = 0;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "deleted_by", nullable = false)
    private Long deletedBy;

}

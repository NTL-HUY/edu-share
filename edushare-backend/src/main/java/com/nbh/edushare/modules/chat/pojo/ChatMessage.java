package com.nbh.edushare.modules.chat.pojo;

import com.nbh.edushare.common.model.SoftDeleteModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "chat_message")
public class ChatMessage extends SoftDeleteModel {

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    // denormalize thông tin người gửi
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "user_avatar_url", length = 255)
    private String userAvatarUrl;

    @Column(name = "client_temp_id", nullable = false, length = 100)
    private String clientTempId;

    // ===== reply
    @Column(name = "reply_to_message_id")
    private Long replyToMessageId;

    @Column(name = "reply_to_user_name", length = 100)
    private String replyToUserName;

    @Column(name = "reply_to_content_preview", length = 200)
    private String replyToContentPreview;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "deleted_by")
    private Long deletedBy;
}
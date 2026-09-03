package com.nbh.edushare.modules.interaction.dto.response;

import com.nbh.edushare.modules.interaction.pojo.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long knowledgeId,
        Long userId,
        String userName,
        String userAvatarUrl,
        Long rootCommentId,
        Long replyToCommentId,
        String replyToUserName,
        Integer replyCount,
        String content,
        LocalDateTime createdAt
) {
    public CommentResponse withDeletedContent() {
        return new CommentResponse(
                id, knowledgeId, null, "Người dùng Edushare", null,
                rootCommentId, replyToCommentId, replyToUserName,
                replyCount, "Bình luận này đã bị xóa", createdAt
        );
    }
}
package com.nbh.edushare.modules.knowledge.port;

import com.nbh.edushare.modules.interaction.dto.response.CommentResponse;

import java.util.Optional;

public interface CommentQueryPort {
    Optional<CommentResponse> getCommentById(Long commentId);
}

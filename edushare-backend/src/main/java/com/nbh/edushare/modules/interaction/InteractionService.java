package com.nbh.edushare.modules.interaction;

import com.nbh.edushare.modules.interaction.dto.request.CreateCommentRequest;
import com.nbh.edushare.modules.interaction.dto.request.VoteRequest;
import com.nbh.edushare.modules.interaction.dto.response.CommentResponse;
import com.nbh.edushare.modules.interaction.dto.response.VoteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface InteractionService {

    CommentResponse createComment(Long knowledgeId, Long userId, CreateCommentRequest request);

    Page<CommentResponse> listRootComments(Long knowledgeId, Pageable pageable);

    List<CommentResponse> listReplies(Long knowledgeId, Long rootCommentId);

    void deleteComment(Long commentId, Long userId);

    VoteResponse vote(Long knowledgeId, Long userId, VoteRequest request);

    VoteResponse unvote(Long knowledgeId, Long userId);

    void recordView(Long knowledgeId);

    <T> Optional<T> findByUserIdAndKnowledgeId(Long userId, Long knowledgeId, Class<T>clazz);
}
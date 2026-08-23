package com.nbh.edushare.modules.interaction;

import com.nbh.edushare.common.exception.AppException;
import com.nbh.edushare.modules.feed.FeedService;
import com.nbh.edushare.modules.feed.dto.request.FeedItemProjection;
import com.nbh.edushare.modules.feed.dto.response.FeedCountProjection;
import com.nbh.edushare.modules.interaction.counter.CounterService;
import com.nbh.edushare.modules.interaction.dto.request.CreateCommentRequest;
import com.nbh.edushare.modules.interaction.dto.request.VoteRequest;
import com.nbh.edushare.modules.interaction.dto.response.CommentResponse;
import com.nbh.edushare.modules.interaction.dto.response.VoteResponse;
import com.nbh.edushare.modules.interaction.event.CommentChangedEvent;
import com.nbh.edushare.modules.interaction.event.ViewRecordedEvent;
import com.nbh.edushare.modules.interaction.event.VoteChangedEvent;
import com.nbh.edushare.modules.interaction.exception.InteractionErrorCode;
import com.nbh.edushare.modules.interaction.mapper.CommentMapper;
import com.nbh.edushare.modules.interaction.pojo.Comment;
import com.nbh.edushare.modules.interaction.pojo.Vote;
import com.nbh.edushare.modules.interaction.repository.CommentRepository;
import com.nbh.edushare.modules.interaction.repository.VoteRepository;
import com.nbh.edushare.modules.knowledge.KnowledgeService;
import com.nbh.edushare.modules.knowledge.dto.response.KnowledgeDetailResponse;
import com.nbh.edushare.modules.knowledge.exception.KnowledgeErrorCode;
import com.nbh.edushare.modules.user.UserService;
import com.nbh.edushare.modules.user.dto.response.UserAuthInfo;
import com.nbh.edushare.modules.user.dto.response.UserBaseProjection;
import com.nbh.edushare.modules.user.enums.UserRole;
import com.nbh.edushare.modules.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InteractionServiceImpl implements InteractionService {
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final FeedService feedService;

    private final ApplicationEventPublisher eventPublisher;

    // ================= COMMENT =================

    @Transactional
    @Override
    public CommentResponse createComment(Long knowledgeId, Long userId, CreateCommentRequest request) {

        FeedItemProjection feed = feedService.findProjectedById(knowledgeId, FeedItemProjection.class)
                .orElseThrow(() -> new AppException(KnowledgeErrorCode.KNOWLEDGE_NOT_FOUND));

        if (!feed.getAllowComment()) {
            throw new AppException(InteractionErrorCode.COMMENT_NOT_ALLOWED);
        }

        UserBaseProjection currentUser = userService.findProjectedById(userId, UserBaseProjection.class)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

        Comment comment = commentMapper.toEntity(request, feed, currentUser);

        // 4. Xử lý logic reply (nếu có replyToCommentId)
        Comment parentComment = null;
        if (request.replyToCommentId() != null) {
            parentComment = commentRepository.findByIdAndDeletedAtIsNull(request.replyToCommentId())
                    .orElseThrow(() -> new AppException(InteractionErrorCode.COMMENT_NOT_FOUND));

            // Đảm bảo comment cha thuộc cùng bài viết
            if (!parentComment.getKnowledgeId().equals(knowledgeId)) {
                throw new AppException(InteractionErrorCode.REPLY_ROOT_MISMATCH);
            }

            // Xác định root comment: nếu cha đã có root thì lấy root đó,
            // nếu cha chính là root (comment gốc) thì dùng id của cha
            Long rootId = parentComment.getRootCommentId() != null
                    ? parentComment.getRootCommentId()
                    : parentComment.getId();

            comment.setRootCommentId(rootId);
            comment.setReplyToUserName(parentComment.getUserName());
        }
        // Nếu không reply -> đây là comment gốc, rootCommentId để null (hoặc set = id sau khi save, tùy convention)

        // 5. Lưu comment
        Comment saved = commentRepository.save(comment);

        // tăng reply_count của comment gốc (nếu đây là reply)
        if (comment.getRootCommentId() != null) {
            commentRepository.adjustReplyCount(comment.getRootCommentId(), 1);
        }

        // tăng tổng số comment của bài viết (Redis) — luôn +1 dù là comment gốc hay reply
        eventPublisher.publishEvent(new CommentChangedEvent(knowledgeId, 1));

        // 7. Trả về response
        return commentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> listRootComments(Long knowledgeId, Pageable pageable) {
        return commentRepository
                .findRootCommentsForFeed(knowledgeId, pageable)
                .map(commentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> listReplies(Long knowledgeId, Long rootCommentId) {
        return commentRepository
                .findByKnowledgeIdAndRootCommentIdAndDeletedAtIsNullOrderByCreatedAtAsc(knowledgeId, rootCommentId)
                .stream().map(commentMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new AppException(InteractionErrorCode.COMMENT_NOT_FOUND));

        UserAuthInfo actor = userService.findProjectedById(userId, UserAuthInfo.class)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

        boolean isAdmin = UserRole.ADMIN.equals(actor.getUserRole());
        boolean isOwner = comment.getUserId().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new AppException(InteractionErrorCode.COMMENT_ACCESS_DENIED);
        }

        comment.setDeletedAt(LocalDateTime.now());
        comment.setDeletedBy(userId);
        commentRepository.save(comment);

        if (comment.getRootCommentId() != null) {
            commentRepository.adjustReplyCount(comment.getRootCommentId(), -1);
            eventPublisher.publishEvent(new CommentChangedEvent(comment.getKnowledgeId(), -1));
        } else if (comment.getReplyCount() == 0) {
            eventPublisher.publishEvent(new CommentChangedEvent(comment.getKnowledgeId(), -1));
        }
    }

    // ================= VOTE =================

    @Override
    @Transactional
    public VoteResponse vote(Long knowledgeId, Long userId, VoteRequest request) {
        short newValue = request.value();
        if (newValue != 1 && newValue != -1) {
            throw new AppException(InteractionErrorCode.INVALID_VOTE_VALUE);
        }

        if (!feedService.existsByKnowledgeId(knowledgeId)) {
            throw new AppException(KnowledgeErrorCode.KNOWLEDGE_NOT_FOUND);
        }

        int delta;
        var existing = voteRepository.findByUserIdAndKnowledgeId(userId, knowledgeId, Vote.class);

        if (existing.isPresent()) {
            Vote v = existing.get();
            if (v.getValue() == newValue) {
                // vote lại đúng giá trị cũ -> không đổi gì
                return new VoteResponse(knowledgeId, newValue, currentScore(knowledgeId));
            }
            delta = newValue - v.getValue(); // vd: từ -1 -> +1 thì delta = +2
            v.setValue(newValue);
            voteRepository.save(v);
        } else {
            Vote v = Vote.builder()
                    .knowledgeId(knowledgeId)
                    .userId(userId)
                    .value(newValue)
                    .build();
            voteRepository.save(v); // unique constraint (user_id, knowledge_id) bảo vệ double-vote
            delta = newValue;
        }

        // vote_score của bài viết: đi qua Redis, KHÔNG update trực tiếp knowledge ở đây
        eventPublisher.publishEvent(new VoteChangedEvent(knowledgeId, delta));

        return new VoteResponse(knowledgeId, newValue, currentScore(knowledgeId));
    }

    @Override
    @Transactional
    public VoteResponse unvote(Long knowledgeId, Long userId) {
        Vote v = voteRepository.findByUserIdAndKnowledgeId(userId, knowledgeId, Vote.class)
                .orElseThrow(() -> new AppException(InteractionErrorCode.VOTE_NOT_FOUND));

        voteRepository.delete(v);
        eventPublisher.publishEvent(new VoteChangedEvent(knowledgeId, -v.getValue()));

        return new VoteResponse(knowledgeId, null, currentScore(knowledgeId));
    }

    private long currentScore(Long knowledgeId) {
        return feedService.findProjectedById(knowledgeId, FeedCountProjection.class)
                .map(FeedCountProjection::getVoteScore)
                .orElse(0);
    }

    // ================= VIEW =================

    @Override
    public void recordView(Long knowledgeId) {
        eventPublisher.publishEvent(new ViewRecordedEvent(knowledgeId));
    }

    @Override
    @Transactional(readOnly = true)
    public <T> Optional<T> findByUserIdAndKnowledgeId(Long userId, Long knowledgeId,  Class<T>clazz) {
        return voteRepository.findByUserIdAndKnowledgeId(userId,knowledgeId, clazz);
    }
}
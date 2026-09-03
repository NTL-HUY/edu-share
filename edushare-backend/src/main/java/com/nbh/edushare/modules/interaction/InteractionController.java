package com.nbh.edushare.modules.interaction;
import com.nbh.edushare.modules.interaction.dto.request.CreateCommentRequest;
import com.nbh.edushare.modules.interaction.dto.request.VoteRequest;
import com.nbh.edushare.modules.interaction.dto.response.CommentResponse;
import com.nbh.edushare.modules.interaction.dto.response.VoteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class InteractionController {

    private final InteractionService interactionService;

    // ================= COMMENT =================

    @PostMapping("/feed/{feedId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(
            @PathVariable Long feedId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        return interactionService.createComment(feedId, userId, request);
    }

    @GetMapping("/feed/{feedId}/comments")
    public Page<CommentResponse> listRootComments(
            @PathVariable Long feedId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return interactionService.listRootComments(feedId, pageable);
    }

    @GetMapping("/feed/{feedId}/comments/{rootCommentId}/replies")
    public List<CommentResponse> listReplies(
            @PathVariable Long feedId,
            @PathVariable Long rootCommentId
    ) {
        return interactionService.listReplies(feedId, rootCommentId);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Long userId
    ) {
        interactionService.deleteComment(commentId, userId);
    }

    // ================= VOTE =================

    @PutMapping("/feed/{feedId}/vote")
    public VoteResponse vote(
            @PathVariable Long feedId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody VoteRequest request
    ) {
        return interactionService.vote(feedId, userId, request);
    }

    @DeleteMapping("/feed/{feedId}/vote")
    public VoteResponse unvote(
            @PathVariable Long feedId,
            @AuthenticationPrincipal Long userId
    ) {
        return interactionService.unvote(feedId, userId);
    }

    // ================= VIEW =================

    @PostMapping("/feed/{feedId}/views")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void recordView(@PathVariable Long feedId) {
        interactionService.recordView(feedId);
    }
}

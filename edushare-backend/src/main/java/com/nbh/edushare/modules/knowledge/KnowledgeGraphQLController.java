package com.nbh.edushare.modules.knowledge;

import com.nbh.edushare.common.utils.CursorPaging;
import com.nbh.edushare.modules.interaction.InteractionService;
import com.nbh.edushare.modules.interaction.dto.request.CommentQueryInput;
import com.nbh.edushare.modules.interaction.dto.request.VoteValueProjection;
import com.nbh.edushare.modules.interaction.dto.response.CommentResponse;
import com.nbh.edushare.modules.knowledge.dto.command.CreateLessonCommand;
import com.nbh.edushare.modules.knowledge.dto.command.CreateQuestionCommand;
import com.nbh.edushare.modules.knowledge.dto.command.UpdateLessonCommand;
import com.nbh.edushare.modules.knowledge.dto.command.UpdateQuestionCommand;
import com.nbh.edushare.modules.knowledge.dto.request.KnowledgeFilterInput;
import com.nbh.edushare.modules.knowledge.dto.response.KnowledgeDetailResponse;
import com.nbh.edushare.modules.knowledge.dto.response.LessonDetailResponse;
import com.nbh.edushare.modules.knowledge.dto.response.QuestionDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class KnowledgeGraphQLController {

    private final KnowledgeService knowledgeService;
    private final InteractionService interactionService;

    @MutationMapping
    public LessonDetailResponse createLesson(
            @Argument("input") CreateLessonCommand command,
            @AuthenticationPrincipal Long userId
    ) {
        return knowledgeService.createLesson(command, userId);
    }

    @MutationMapping
    public QuestionDetailResponse createQuestion(
            @Argument("input") CreateQuestionCommand command,
            @AuthenticationPrincipal Long userId
    ) {
        return knowledgeService.createQuestion(command, userId);
    }

    @MutationMapping
    public LessonDetailResponse updateLesson(@Argument("input") UpdateLessonCommand command, @AuthenticationPrincipal Long userId) {
        return knowledgeService.updateLesson(command, userId);
    }

    @MutationMapping
    public QuestionDetailResponse updateQuestion(@Argument("input") UpdateQuestionCommand command, @AuthenticationPrincipal Long userId) {
        return knowledgeService.updateQuestion(command, userId);
    }

    @QueryMapping
    public KnowledgeDetailResponse knowledge(@Argument Long id, @AuthenticationPrincipal Long currentUserId) {
        return knowledgeService.getKnowledgeDetailForView(id, currentUserId);
    }

    @SchemaMapping(typeName = "Knowledge", field = "comments")
    public CursorPaging<CommentResponse> comments(
            KnowledgeDetailResponse knowledge,
            @Argument CommentQueryInput input
    ) {
        Long knowledgeId = knowledge.getId();
        return interactionService.listRootComments(
                knowledgeId,
                input
        );
    }

    @SchemaMapping(typeName = "Knowledge", field = "currentUserVote")
    public Short currentUserVote(
            KnowledgeDetailResponse knowledge,
            @AuthenticationPrincipal Long currentUserId
    ) {
        if (currentUserId == null) return (short) 0;
        return interactionService
                .findByUserIdAndKnowledgeId(currentUserId, knowledge.getId(), VoteValueProjection.class)
                .map(VoteValueProjection::getValue)
                .orElse((short) 0);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public Page<KnowledgeDetailResponse> myKnowledgeList(
            @Argument KnowledgeFilterInput input,
            @AuthenticationPrincipal Long currentUserId
    ) {
        return knowledgeService.getMyKnowledgeList(currentUserId, input);
    }

    @QueryMapping
    public Page<KnowledgeDetailResponse> knowledgeListByUsername(
            @Argument String username,
            @Argument KnowledgeFilterInput input
    ) {
        return knowledgeService.getKnowledgeListByUsername(username, input);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean deleteKnowledge(
            @Argument Long id,
            @AuthenticationPrincipal Long currentUserId
    ) {
        knowledgeService.deleteKnowledge(id,currentUserId);
        return true;
    }
}
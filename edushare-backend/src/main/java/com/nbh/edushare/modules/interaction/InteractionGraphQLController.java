package com.nbh.edushare.modules.interaction;


import com.nbh.edushare.common.dto.PageableInput;
import com.nbh.edushare.common.utils.CursorPaging;
import com.nbh.edushare.modules.interaction.dto.request.CommentFilterInput;
import com.nbh.edushare.modules.interaction.dto.request.CommentQueryInput;
import com.nbh.edushare.modules.interaction.dto.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InteractionGraphQLController {
    private final InteractionService interactionService;

    @QueryMapping
    public CursorPaging<CommentResponse> listRootComments(
            @Argument Long knowledgeId,
            @Argument CommentQueryInput input
    ) {
        return interactionService.listRootComments(knowledgeId, input);
    }

    @QueryMapping
    public List<CommentResponse> listCommentReplies(
            @Argument Long knowledgeId,
            @Argument Long rootCommentId
    ) {
        return interactionService.listReplies(knowledgeId, rootCommentId);
    }

}

package com.nbh.edushare.modules.interaction.mapper;

import com.nbh.edushare.modules.feed.dto.request.FeedItemProjection;
import com.nbh.edushare.modules.interaction.dto.request.CreateCommentRequest;
import com.nbh.edushare.modules.interaction.dto.response.CommentResponse;
import com.nbh.edushare.modules.interaction.pojo.Comment;
import com.nbh.edushare.modules.knowledge.dto.response.KnowledgeDetailResponse;
import com.nbh.edushare.modules.user.dto.response.UserBaseProjection;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.WARN)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "knowledgeId", source = "feed.knowledgeId")
    @Mapping(target = "userId", source = "currentUser.id")
    @Mapping(target = "userName", source = "currentUser.username")
    @Mapping(target = "userAvatarUrl", source = "currentUser.avatarUrl")
    @Mapping(target = "content", source = "request.content")
    @Mapping(target = "replyToCommentId", source = "request.replyToCommentId")
    @Mapping(target = "rootCommentId", ignore = true)
    @Mapping(target = "replyToUserName", ignore = true)
    @Mapping(target = "replyCount", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    Comment toEntity(CreateCommentRequest request, FeedItemProjection feed, UserBaseProjection currentUser);

    CommentResponse mapToResponse(Comment comment);

    default CommentResponse toResponse(Comment comment) {
        if (comment == null) return null;
        CommentResponse response = mapToResponse(comment);
        return (comment.getDeletedAt() != null) ? response.withDeletedContent() : response;
    }
}

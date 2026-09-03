package com.nbh.edushare.modules.knowledge.mapper;

import com.nbh.edushare.modules.knowledge.event.create.LessonCreatedEvent;
import com.nbh.edushare.modules.knowledge.event.create.QuestionCreatedEvent;
import com.nbh.edushare.modules.knowledge.event.update.LessonUpdatedEvent;
import com.nbh.edushare.modules.knowledge.event.update.QuestionUpdatedEvent;
import com.nbh.edushare.modules.knowledge.pojo.Knowledge;
import com.nbh.edushare.modules.knowledge.pojo.Lesson;
import com.nbh.edushare.modules.knowledge.pojo.Question;
import com.nbh.edushare.modules.user.dto.response.UserAuthInfo;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KnowledgeEventMapper {
    @Mapping(target = "knowledgeId", source = "lesson.id")
    @Mapping(target = "ownerId", source = "ownerInfo.id")
    @Mapping(target = "ownerName", source = "ownerInfo.username")
    @Mapping(target = "isFamous", source = "ownerInfo.isFamous")
    @Mapping(target = "ownerAvatarUrl", source = "ownerInfo.avatarUrl")
    @Mapping(target = "categoryId", source = "lesson.category.id")
    @Mapping(target = "categoryName", source = "lesson.category.name")
    LessonCreatedEvent toLessonCreatedEvent(Lesson lesson, UserAuthInfo ownerInfo);

    @Mapping(target = "knowledgeId", source = "question.id")
    @Mapping(target = "ownerId", source = "ownerInfo.id")
    @Mapping(target = "ownerName", source = "ownerInfo.username")
    @Mapping(target = "ownerAvatarUrl", source = "ownerInfo.avatarUrl")
    @Mapping(target = "isFamous", source = "ownerInfo.isFamous")
    @Mapping(target = "categoryId", source = "question.category.id")
    @Mapping(target = "categoryName", source = "question.category.name")
    QuestionCreatedEvent toQuestionCreatedEvent(Question question, UserAuthInfo ownerInfo);

    @Mapping(target = "knowledgeId", source = "id")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    LessonUpdatedEvent toLessonUpdatedEvent(Lesson lesson);

    @Mapping(target = "knowledgeId", source = "id")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    QuestionUpdatedEvent toQuestionUpdatedEvent(Question question);
}
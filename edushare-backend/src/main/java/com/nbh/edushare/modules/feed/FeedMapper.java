package com.nbh.edushare.modules.feed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbh.edushare.modules.feed.pojo.FeedItem;
import com.nbh.edushare.modules.feed.pojo.LessonFeedMeta;
import com.nbh.edushare.modules.feed.pojo.QuestionFeedMeta;
import com.nbh.edushare.modules.knowledge.event.create.KnowledgeCreatedEvent;
import com.nbh.edushare.modules.knowledge.event.create.LessonCreatedEvent;
import com.nbh.edushare.modules.knowledge.event.create.QuestionCreatedEvent;
import com.nbh.edushare.modules.knowledge.event.update.KnowledgeUpdatedEvent;
import com.nbh.edushare.modules.knowledge.event.update.LessonUpdatedEvent;
import com.nbh.edushare.modules.knowledge.event.update.QuestionUpdatedEvent;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring" , imports = {LocalDateTime.class})
public abstract class FeedMapper {

    @Autowired
    private ObjectMapper objectMapper;

    public FeedItem toEntity(KnowledgeCreatedEvent event){
        if(event == null) return null;
        return switch (event) {
            case LessonCreatedEvent lesson -> toEntity(lesson);
            case QuestionCreatedEvent question -> toEntity(question);
            default -> throw new IllegalStateException("Unexpected value: " + event);
        };
    }

    @Mapping(target = "sourceCreatedAt", source = "createdAt")
    @Mapping(target = "viewsCount", constant = "0")
    @Mapping(target = "voteScore", constant = "0")
    @Mapping(target = "commentCount", constant = "0")
    @Mapping(target = "typeMeta", source = ".")
    @Mapping(target = "syncedAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "deletedAt", ignore = true)
    public abstract FeedItem toEntity(LessonCreatedEvent lesson);


    @Mapping(target = "sourceCreatedAt", source = "createdAt")
    @Mapping(target = "viewsCount", constant = "0")
    @Mapping(target = "voteScore", constant = "0")
    @Mapping(target = "commentCount", constant = "0")
    @Mapping(target = "typeMeta", source = ".")
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "syncedAt", expression = "java(LocalDateTime.now())")
    public abstract FeedItem toEntity(QuestionCreatedEvent question);

    protected JsonNode mapTypeMeta(LessonCreatedEvent event){
        LessonFeedMeta lessonFeedMeta = toLessonMeta(event);
        return objectMapper.valueToTree(lessonFeedMeta);
    }

    protected JsonNode mapTypeMeta(QuestionCreatedEvent event){
        QuestionFeedMeta questionFeedMeta = toQuestionMeta(event);
        return objectMapper.valueToTree(questionFeedMeta);
    }

    public abstract LessonFeedMeta toLessonMeta(LessonCreatedEvent lesson);
    public abstract QuestionFeedMeta toQuestionMeta(QuestionCreatedEvent question);

    public Object mapJsonNodeToTypeMeta(FeedItem entity) {
        if (entity.getTypeMeta() == null || entity.getType() == null) {
            return null;
        }
        try {
            return switch (entity.getType()) {
                case LESSON -> objectMapper.treeToValue(entity.getTypeMeta(), LessonFeedMeta.class);
                case QUESTION -> objectMapper.treeToValue(entity.getTypeMeta(), QuestionFeedMeta.class);
            };
        } catch (Exception e) {
            return null;
        }
    }



    public void updateEntity(FeedItem entity, KnowledgeUpdatedEvent event) {
        if (event == null) return;
        switch (event) {
            case LessonUpdatedEvent lesson -> updateEntity(entity, lesson);
            case QuestionUpdatedEvent question -> updateEntity(entity, question);
        }
    }

    @Mapping(target = "knowledgeId", ignore = true)
    @Mapping(target = "sourceCreatedAt", ignore = true)
    @Mapping(target = "viewsCount", ignore = true)
    @Mapping(target = "voteScore", ignore = true)
    @Mapping(target = "commentCount", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "typeMeta", source = ".")
    @Mapping(target = "syncedAt", expression = "java(LocalDateTime.now())")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEntity(@MappingTarget FeedItem entity, LessonUpdatedEvent lesson);

    @Mapping(target = "knowledgeId", ignore = true)
    @Mapping(target = "sourceCreatedAt", ignore = true)
    @Mapping(target = "viewsCount", ignore = true)
    @Mapping(target = "voteScore", ignore = true)
    @Mapping(target = "commentCount", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "typeMeta", source = ".")
    @Mapping(target = "syncedAt", expression = "java(LocalDateTime.now())")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEntity(@MappingTarget FeedItem entity, QuestionUpdatedEvent question);

    protected JsonNode mapTypeMeta(LessonUpdatedEvent event){
        LessonFeedMeta meta = toLessonMeta(event);
        return objectMapper.valueToTree(meta);
    }

    protected JsonNode mapTypeMeta(QuestionUpdatedEvent event){
        QuestionFeedMeta meta = toQuestionMeta(event);
        return objectMapper.valueToTree(meta);
    }

    public abstract LessonFeedMeta toLessonMeta(LessonUpdatedEvent lesson);
    public abstract QuestionFeedMeta toQuestionMeta(QuestionUpdatedEvent question);

}

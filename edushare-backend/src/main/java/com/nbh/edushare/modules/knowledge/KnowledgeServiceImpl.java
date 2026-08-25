package com.nbh.edushare.modules.knowledge;

import com.nbh.edushare.common.exception.AppException;
import com.nbh.edushare.modules.knowledge.dto.command.UpdateLessonCommand;
import com.nbh.edushare.modules.knowledge.dto.command.UpdateQuestionCommand;
import com.nbh.edushare.modules.knowledge.dto.request.KnowledgeFilterInput;
import com.nbh.edushare.modules.knowledge.dto.response.KnowledgeDetailResponse;
import com.nbh.edushare.modules.knowledge.dto.response.KnowledgeManageProjection;
import com.nbh.edushare.modules.knowledge.event.update.KnowledgeUpdatedEvent;
import com.nbh.edushare.modules.knowledge.exception.CategoryErrorCode;
import com.nbh.edushare.modules.knowledge.exception.KnowledgeErrorCode;
import com.nbh.edushare.modules.knowledge.dto.command.CreateLessonCommand;
import com.nbh.edushare.modules.knowledge.dto.command.CreateQuestionCommand;
import com.nbh.edushare.modules.knowledge.dto.response.LessonDetailResponse;
import com.nbh.edushare.modules.knowledge.dto.response.QuestionDetailResponse;
import com.nbh.edushare.modules.knowledge.event.create.KnowledgeCreatedEvent;
import com.nbh.edushare.modules.knowledge.mapper.KnowledgeEventMapper;
import com.nbh.edushare.modules.knowledge.mapper.LessonMapper;
import com.nbh.edushare.modules.knowledge.mapper.QuestionMapper;
import com.nbh.edushare.modules.knowledge.pojo.*;
import com.nbh.edushare.modules.knowledge.repository.CategoryRepository;
import com.nbh.edushare.modules.knowledge.repository.KnowledgeRepository;
import com.nbh.edushare.modules.knowledge.repository.LessonRepository;
import com.nbh.edushare.modules.knowledge.repository.QuestionRepository;
import com.nbh.edushare.modules.user.UserService;
import com.nbh.edushare.modules.user.dto.response.UserAuthInfo;
import com.nbh.edushare.modules.user.exception.UserErrorCode;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
class KnowledgeServiceImpl implements KnowledgeService {
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final LessonRepository lessonRepository;
    private final QuestionMapper questionMapper;
    private final EntityManager entityManager;
    private final LessonMapper lessonMapper;
    private final KnowledgeRepository knowledgeRepository;

    private final UserService userService;
    private final KnowledgeEventMapper knowledgeEventMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public LessonDetailResponse createLesson(CreateLessonCommand command, Long ownerId) {
        Lesson lesson = lessonMapper.toEntity(command);
        Lesson saved = saveKnowledge(lesson, command.categoryId(), ownerId, lessonRepository);
        return lessonMapper.toDetailResponse(saved);
    }


    @Override
    @Transactional
    public QuestionDetailResponse createQuestion(CreateQuestionCommand command, Long ownerId) {
        Question question = questionMapper.toEntity(command);
        Question saved = saveKnowledge(question, command.categoryId(), ownerId, questionRepository);
        return questionMapper.toDetailResponse(saved);
    }

    @Override
    public KnowledgeDetailResponse getKnowledgeDetailForEdit(Long id, Long currentUserId) {
        Knowledge knowledge = knowledgeRepository.findDetailById(id)
                .orElseThrow(() -> new AppException(KnowledgeErrorCode.KNOWLEDGE_NOT_FOUND));

        if (!knowledge.getOwner().getId().equals(currentUserId)) {
            throw new AppException(KnowledgeErrorCode.KNOWLEDGE_ACCESS_DENIED);
        }

        return builDetailResponse(knowledge);
    }

    @Override
    @Transactional(readOnly = true)
    public KnowledgeDetailResponse getKnowledgeDetailForView(Long id, Long currentUserId) {
        Knowledge knowledge = knowledgeRepository.findDetailById(id)
                .orElseThrow(() -> new AppException(KnowledgeErrorCode.KNOWLEDGE_NOT_FOUND));

        boolean isOwner = knowledge.getOwner().getId().equals(currentUserId);
        if (!knowledge.getIsPublic() && !isOwner) {
            throw new AppException(KnowledgeErrorCode.KNOWLEDGE_ACCESS_DENIED);
        }
        return builDetailResponse(knowledge);
    }

    @Override
    @Transactional
    public LessonDetailResponse updateLesson(UpdateLessonCommand command, Long currentUserId) {
        Lesson lesson = updateKnowledge(
                command.id(),
                command.categoryId(),
                currentUserId,
                lessonRepository,
                target -> lessonMapper.updateEntityFromCommand(command, target)
        );
        return lessonMapper.toDetailResponse(lesson);
    }

    @Override
    @Transactional
    public QuestionDetailResponse updateQuestion(UpdateQuestionCommand command, Long currentUserId) {
        Question question = updateKnowledge(
                command.id(),
                command.categoryId(),
                currentUserId,
                questionRepository,
                target -> questionMapper.updateEntityFromCommand(command, target)
        );
        return questionMapper.toDetailResponse(question);
    }

    @Override
    @Transactional
    public int adjustCounters(Long id, int views, int votes, int comments) {
        return knowledgeRepository.adjustCounters(id, views, votes, comments);
    }

    @Transactional(readOnly = true)
    @Override
    public KnowledgeDetailResponse findById(Long knowledgeId) {
        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new AppException(KnowledgeErrorCode.KNOWLEDGE_NOT_FOUND));
        return builDetailResponse(knowledge);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeDetailResponse> getMyKnowledgeList(Long userId, KnowledgeFilterInput filter) {
        Page<Knowledge> page = knowledgeRepository.findByOwnerIdAndDeletedAtIsNull(userId, filter.toPageable());
        return page.map(this::builDetailResponse);
    }


    private <T extends Knowledge> T saveKnowledge(T entity, Long categoryId, Long ownerId, JpaRepository<T, Long> repository) {
        UserAuthInfo ownerInfo = userService.findProjectedById(ownerId, UserAuthInfo.class)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

        UserRef owner = entityManager.getReference(UserRef.class, ownerId);
        entity.setOwner(owner);

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(CategoryErrorCode.CATEGORY_NOT_FOUND));
            entity.setCategory(category);
        }

        T saved = repository.save(entity);
        eventPublisher.publishEvent(buildCreatedEvent(saved, ownerInfo));
        return saved;
    }

    private <T extends Knowledge> T updateKnowledge(
            Long id,
            Long categoryId,
            Long currentUserId,
            JpaRepository<T, Long> repository,
            Consumer<T> entityUpdater
    ) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new AppException(KnowledgeErrorCode.KNOWLEDGE_NOT_FOUND));

        if (!entity.getOwner().getId().equals(currentUserId)) {
            throw new AppException(KnowledgeErrorCode.KNOWLEDGE_ACCESS_DENIED);
        }

        entityUpdater.accept(entity);

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(CategoryErrorCode.CATEGORY_NOT_FOUND));
            entity.setCategory(category);
        }

        T updated = repository.save(entity);
        eventPublisher.publishEvent(buildUpdatedEvent(updated));

        return updated;
    }


    private KnowledgeCreatedEvent buildCreatedEvent(Knowledge saved, UserAuthInfo ownerInfo) {
        return switch (saved) {
            case Lesson lesson -> knowledgeEventMapper.toLessonCreatedEvent(lesson, ownerInfo);
            case Question question -> knowledgeEventMapper.toQuestionCreatedEvent(question, ownerInfo);
            default -> throw new IllegalStateException("Unsupported knowledge type");
        };
    }

    private KnowledgeUpdatedEvent buildUpdatedEvent(Knowledge updated) {
        return switch (updated) {
            case Lesson lesson -> knowledgeEventMapper.toLessonUpdatedEvent(lesson);
            case Question question -> knowledgeEventMapper.toQuestionUpdatedEvent(question);
            default -> throw new IllegalStateException("Unsupported knowledge type");
        };
    }

    private KnowledgeDetailResponse builDetailResponse(Knowledge response) {
        return switch (response) {
            case Lesson lesson -> lessonMapper.toDetailResponse(lesson);
            case Question question -> questionMapper.toDetailResponse(question);
            default -> throw new IllegalStateException("Unsupported knowledge type");
        };
    }
}

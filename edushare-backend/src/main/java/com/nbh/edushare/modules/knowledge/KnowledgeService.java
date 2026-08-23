package com.nbh.edushare.modules.knowledge;

import com.nbh.edushare.modules.knowledge.dto.command.*;
import com.nbh.edushare.modules.knowledge.dto.response.CourseDetailResponse;
import com.nbh.edushare.modules.knowledge.dto.response.KnowledgeDetailResponse;
import com.nbh.edushare.modules.knowledge.dto.response.LessonDetailResponse;
import com.nbh.edushare.modules.knowledge.dto.response.QuestionDetailResponse;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface KnowledgeService {
    LessonDetailResponse createLesson(CreateLessonCommand command, Long ownerId);
    QuestionDetailResponse createQuestion(CreateQuestionCommand command, Long ownerId);
    KnowledgeDetailResponse getKnowledgeDetailForEdit(Long id, Long currentUserId);
    KnowledgeDetailResponse getKnowledgeDetailForView(Long id, Long currentUserId);
    LessonDetailResponse updateLesson(UpdateLessonCommand command, Long currentUserId);
    QuestionDetailResponse updateQuestion(UpdateQuestionCommand command, Long currentUserId);
    int adjustCounters(Long id, int views, int votes, int comments);

    KnowledgeDetailResponse findById(Long knowledgeId);
}

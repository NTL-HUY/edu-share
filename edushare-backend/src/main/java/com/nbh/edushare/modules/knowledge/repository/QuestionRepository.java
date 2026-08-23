package com.nbh.edushare.modules.knowledge.repository;

import com.nbh.edushare.modules.knowledge.pojo.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT q FROM Question q " +
            "LEFT JOIN FETCH q.category " +
            "LEFT JOIN FETCH q.owner " +
            "WHERE q.id = :id")
    Optional<Question> findDetailById(@Param("id") Long id);
}

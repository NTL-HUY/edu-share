package com.nbh.edushare.modules.knowledge.repository;

import com.nbh.edushare.modules.knowledge.pojo.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @Query("SELECT l FROM Lesson l " +
            "LEFT JOIN FETCH l.category " +
            "LEFT JOIN FETCH l.owner " +
            "WHERE l.id = :id")
    Optional<Lesson> findDetailById(@Param("id") Long id);
}

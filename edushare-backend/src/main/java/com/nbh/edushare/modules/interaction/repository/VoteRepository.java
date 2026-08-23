package com.nbh.edushare.modules.interaction.repository;

import com.nbh.edushare.modules.interaction.pojo.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    <T> Optional<T> findByUserIdAndKnowledgeId(Long userId, Long knowledgeId, Class<T> type);
}
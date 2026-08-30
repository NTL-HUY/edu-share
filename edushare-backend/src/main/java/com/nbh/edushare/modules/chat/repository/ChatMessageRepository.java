package com.nbh.edushare.modules.chat.repository;

import com.nbh.edushare.modules.chat.pojo.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("""
                SELECT m FROM ChatMessage m\s
                WHERE m.roomId = :roomId\s
                AND (:beforeId IS NULL OR m.id < :beforeId)\s
                ORDER BY m.id DESC
           """)
    List<ChatMessage> findMessagesByCursor(
            @Param("roomId") Long roomId,
            @Param("beforeId") Long beforeId,
            Pageable pageable
    );
}

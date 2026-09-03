package com.nbh.edushare.modules.chat.repository;

import com.nbh.edushare.modules.chat.dto.RoomUnreadProjection;
import com.nbh.edushare.modules.chat.pojo.RoomReadState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomReadStateRepository extends JpaRepository<RoomReadState, Long> {
    @Modifying
    @Query("""
        UPDATE RoomReadState r 
        SET r.lastReadMessageId = :messageId 
        WHERE r.roomId = :roomId 
          AND r.userId = :userId 
          AND (r.lastReadMessageId IS NULL OR r.lastReadMessageId < :messageId)
    """)
    int updateLastReadIfGreater(
            @Param("roomId") Long roomId,
            @Param("userId") Long userId,
            @Param("messageId") Long messageId
    );

    @Query("""
        SELECT 
            m.roomId AS roomId, 
            COUNT(m.id) AS unreadCount
        FROM ChatMessage m
        LEFT JOIN RoomReadState rrs 
            ON rrs.roomId = m.roomId AND rrs.userId = :userId
        WHERE m.deletedAt IS NULL 
          AND m.id > COALESCE(rrs.lastReadMessageId, 0)
        GROUP BY m.roomId
    """)
    List<RoomUnreadProjection> getUnreadCountsByUserId(@Param("userId") Long userId);

    Optional<RoomReadState> findByRoomIdAndUserId(Long roomId, Long userId);
}

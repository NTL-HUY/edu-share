package com.nbh.edushare.modules.chat.repository;

import com.nbh.edushare.modules.chat.pojo.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    List<ChatRoom> findByIsActiveTrue();
    boolean existsByIdAndIsActiveTrue(Long id);
    Optional<ChatRoom> findByIdAndIsActiveTrue( Long id);
}

package com.nbh.edushare.modules.chat.pojo;

import com.nbh.edushare.common.model.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "room_read_state",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_room_read_state_room_user",
                columnNames = {"room_id", "user_id"}
        )
)
public class RoomReadState extends BaseModel {

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    @UpdateTimestamp
    @Column(name = "last_visited_at", nullable = false)
    private LocalDateTime lastVisitedAt = LocalDateTime.now();
}
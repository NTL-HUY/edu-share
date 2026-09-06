package com.nbh.edushare.modules.knowledge.pojo;

import com.nbh.edushare.modules.knowledge.enums.LessonLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lesson")
@PrimaryKeyJoinColumn(name = "knowledge_id")
@Getter
@Setter
@NoArgsConstructor
public class Lesson extends Knowledge {

    @Column(name = "content_markdown")
    private String contentMarkdown;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private LessonLevel level;


    @Column(name = "estimate_time_in_minutes")
    private Integer estimateTimeInMinutes;

}
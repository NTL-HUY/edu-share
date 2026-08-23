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

//    @JdbcTypeCode(SqlTypes.JSON)
//    @Column(name = "toc_json")
//    private String tocJson; // hoặc map sang Map<String,Object> nếu cần thao tác cấu trúc

//    @Column(name = "video_url")
//    private String videoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private LessonLevel level;

//    @Column(name = "is_preview", nullable = false)
//    private Boolean isPreview;

    @Column(name = "estimate_time_in_minutes")
    private Integer estimateTimeInMinutes;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "course_id", referencedColumnName = "knowledge_id")
//    private Course course;

//    @Column(name = "order_index")
//    private Integer orderIndex;
}
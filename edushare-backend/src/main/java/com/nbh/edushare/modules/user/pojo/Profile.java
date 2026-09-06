package com.nbh.edushare.modules.user.pojo;


import com.nbh.edushare.common.model.TimeStampedModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
public class Profile extends TimeStampedModel {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @MapsId
    private User user;

    @Column(length = 20)
    private String studentId;

    @Column(length = 100)
    private String university;

    @Column(length = 100)
    private String faculty;

    @Column(length = 100)
    private String major;

    @Column(length = 20)
    private String className;

    @Column(length = 10)
    private String academicYear;

    @Column(precision = 3, scale = 2)
    private BigDecimal cpa;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String coverUrl;

}

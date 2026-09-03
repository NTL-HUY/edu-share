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
    private String studentId; // MSSV: 2351050055

    @Column(length = 100)
    private String university; // Trường: Đại học Mở TP.HCM

    @Column(length = 100)
    private String faculty; // Khoa: Công nghệ Thông tin

    @Column(length = 100)
    private String major; // Ngành: Kỹ thuật Phần mềm

    @Column(length = 20)
    private String className; // Lớp: DH23IT01

    @Column(length = 10)
    private String academicYear; // Niên khóa: K23

    @Column(precision = 3, scale = 2)
    private BigDecimal cpa; // Điểm CPA: 3.62

    @Column(columnDefinition = "TEXT")
    private String bio; // Giới thiệu bản thân

    private String coverUrl;

}

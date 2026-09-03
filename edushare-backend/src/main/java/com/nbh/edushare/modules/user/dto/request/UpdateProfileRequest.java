package com.nbh.edushare.modules.user.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateProfileRequest(
        @NotBlank(message = "Họ và tên không được để trống")
        @Size(max = 100, message = "Họ và tên không được vượt quá 100 ký tự")
        String fullName,

        @Size(max = 255, message = "Đường dẫn ảnh đại diện không được vượt quá 255 ký tự")
        String avatarUrl,

        @Size(max = 20, message = "Mã số sinh viên không được vượt quá 20 ký tự")
        String studentId,

        @Size(max = 100, message = "Tên trường đại học không được vượt quá 100 ký tự")
        String university,

        @Size(max = 100, message = "Tên khoa không được vượt quá 100 ký tự")
        String faculty,

        @Size(max = 100, message = "Tên ngành học không được vượt quá 100 ký tự")
        String major,

        @Size(max = 20, message = "Tên lớp không được vượt quá 20 ký tự")
        String className,

        @Size(max = 10, message = "Khóa/Năm học không được vượt quá 10 ký tự")
        String academicYear,

        @DecimalMin(value = "0.0", message = "CPA phải lớn hơn hoặc bằng 0.0")
        @DecimalMax(value = "4.0", message = "CPA phải nhỏ hơn hoặc bằng 4.0")
        BigDecimal cpa,

        String bio,

        @Size(max = 255, message = "Đường dẫn ảnh bìa không được vượt quá 255 ký tự")
        String coverUrl
) {}
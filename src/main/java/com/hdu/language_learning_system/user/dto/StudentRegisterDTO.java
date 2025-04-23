package com.hdu.language_learning_system.user.dto;

import lombok.Data;

@Data
public class StudentRegisterDTO {
    private String username;
    private String phoneNumber;
    private String description; // 可填写学员备注信息，如报名课程
    private Integer lessonHours; // 1对1课时数
}

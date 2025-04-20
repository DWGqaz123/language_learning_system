package com.hdu.language_learning_system.course.dto;

import lombok.Data;

@Data
public class UserCourseDTO {
    private Integer courseId;
    private String courseName;
    private String courseType;
    private String classGroupCode;
}
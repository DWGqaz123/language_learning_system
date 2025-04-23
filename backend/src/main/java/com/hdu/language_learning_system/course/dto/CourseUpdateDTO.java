package com.hdu.language_learning_system.course.dto;

import lombok.Data;

@Data
public class CourseUpdateDTO {
    private Integer courseId;
    private String courseName;
    private String courseType; // "班级" or "1对1"
    private String courseContent;
    private String classGroupCode;
    private Integer studentId;     // 仅 1 对 1 课程使用
    private Integer totalHours;    // 仅 1 对 1 课程使用
}
package com.hdu.language_learning_system.course.dto;

import lombok.Data;

@Data
public class CourseListDTO {
    private Integer courseId;
    private String courseName;
    private String courseType;
    private String courseContent;
    private Integer totalHours;
    private Integer remainingHours;
    private String classGroupCode;
    private String studentName; // 仅限1对1课程显示
    private Integer studentId; //
}
package com.hdu.language_learning_system.course.dto;

import lombok.Data;

@Data
public class CourseDTO {
    private Integer courseId;
    private String courseName;
    private String courseType;
    private String courseContent;
    private Integer studentId; // 1对1课程绑定的学员ID
    private Integer totalHours;    // 所需课时
    private String classGroupCode; // 班级编号（如T01），1对1课程可为空
}

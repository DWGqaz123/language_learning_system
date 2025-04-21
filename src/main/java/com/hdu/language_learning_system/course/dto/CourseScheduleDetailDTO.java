package com.hdu.language_learning_system.course.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CourseScheduleDetailDTO {
    private Integer scheduleId;
    private Timestamp classTime;
    private String courseName;
    private String courseType;
    private String teacherName;
    private Integer teacherId;
    private String assistantName;
    private Integer assistantId;
    private String roomName;
}
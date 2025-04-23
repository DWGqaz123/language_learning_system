package com.hdu.language_learning_system.course.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CourseScheduleDTO {
    private Integer scheduleId;
    private Timestamp classTime;
    private String courseName;
    private String roomName;
    private Integer teacherId;
    private String teacherName;
    private Integer assistantId;
    private String assistantName;

}
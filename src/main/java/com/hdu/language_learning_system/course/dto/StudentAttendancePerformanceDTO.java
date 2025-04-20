package com.hdu.language_learning_system.course.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class StudentAttendancePerformanceDTO {
    private Integer scheduleId;
    private Timestamp classTime;
    private String courseName;
    private String attendStatus;
    private String performanceEval;
}
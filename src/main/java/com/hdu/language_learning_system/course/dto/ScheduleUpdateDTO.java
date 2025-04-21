package com.hdu.language_learning_system.course.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ScheduleUpdateDTO {
    private Integer scheduleId;
    private Integer teacherId;
    private Integer assistantId;
    private Integer roomId;
    private Timestamp classTime;
}
package com.hdu.language_learning_system.course.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleDTO {
    private Integer scheduleId;
    private Integer courseId;
    private Integer teacherId;
    private String className;
    private LocalDateTime classTime;
    private Integer roomId;
    private Integer totalHours;
    private Integer remainingHours;
}
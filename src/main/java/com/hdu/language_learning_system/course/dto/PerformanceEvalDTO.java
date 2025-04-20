package com.hdu.language_learning_system.course.dto;

import lombok.Data;

@Data
public class PerformanceEvalDTO {
    private Integer scheduleId;
    private Integer studentId;
    private String performanceEval;
}
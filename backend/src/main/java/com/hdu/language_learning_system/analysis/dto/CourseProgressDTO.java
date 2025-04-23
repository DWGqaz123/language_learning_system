package com.hdu.language_learning_system.analysis.dto;

import lombok.Data;

@Data
public class CourseProgressDTO {
    private Integer studentId;
    private Integer totalHours;
    private Integer remainingHours;
    private Integer completedHours;
    private Integer completedPercentage; // 已完成百分比
}
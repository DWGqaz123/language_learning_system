package com.hdu.language_learning_system.analysis.dto;

import lombok.Data;

@Data
public class TaskStatisticsDTO {
    private Integer studentId;
    private Integer totalTasks;
    private Integer submittedTasks;
    private Double completionRate; // 单位：百分比
    private Double averageScore;
}
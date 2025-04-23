package com.hdu.language_learning_system.task.dto;

import lombok.Data;

@Data
public class TaskStatsDTO {
    private Integer studentId;
    private Long totalTasks;
    private Long completedTasks;
    private Long incompleteTasks;
    private Double averageScore;
    private Double submitRate;
}
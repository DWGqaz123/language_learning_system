package com.hdu.language_learning_system.task.dto;

import lombok.Data;

@Data
public class TaskGradingDTO {
    private Integer taskId;
    private Integer studentId;
    private Integer graderId;        // 批改人ID
    private Integer score;
    private String gradeComment;
}
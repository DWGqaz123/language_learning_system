package com.hdu.language_learning_system.task.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TaskUpdateDTO {
    private Integer taskId;
    private String taskContent;
    private Timestamp deadline;
}
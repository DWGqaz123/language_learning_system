package com.hdu.language_learning_system.task.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TaskPublishDTO {
    private Integer scheduleId;     // 可为空，仅课后作业填写
    private Integer studentId;      // 可为空，课后作业不填，训练任务填写
    private Integer publisherId;    // 助教ID
    private String taskType;        // “课后作业”或“训练任务”
    private String taskContent;
    private Timestamp deadline;
}
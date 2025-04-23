package com.hdu.language_learning_system.task.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TaskListDTO {
    private Integer taskId;
    private String taskType;
    private String taskContent;
    private String completionStatus;
    private Timestamp deadline;         // 新增：截止时间
    private Timestamp publishTime;
//    private Timestamp submitTime;
//    private String attachmentPath;
//    private Integer score;
//    private String gradeComment;
}

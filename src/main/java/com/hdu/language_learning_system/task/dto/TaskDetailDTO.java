package com.hdu.language_learning_system.task.dto;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class TaskDetailDTO {
    private Integer taskId;
    private String taskType;
    private String taskContent;
    private Timestamp publishTime;
    private Timestamp deadline;

    // 学员提交信息（如果有）
    private String completionStatus;
    private Timestamp submitTime;
    private String submitText;
    private String attachmentPath;
    private Integer score;
    private String gradeComment;
}
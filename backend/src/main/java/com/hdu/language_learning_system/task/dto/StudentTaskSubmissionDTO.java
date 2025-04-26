package com.hdu.language_learning_system.task.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class StudentTaskSubmissionDTO {
    private Integer studentId;
    private Integer taskId;
    private String submitText;      // 作业文本内容
    private Timestamp submitTime;   // 提交时间
    private Integer score;          // 分数
    private String gradeComment;    // 批改点评
}

package com.hdu.language_learning_system.task.dto;
import java.sql.Timestamp;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TaskSubmissionDTO {
    private Integer taskId;
    private Integer studentId;
    private String attachmentPath;
    private String submitText; // 提交说明
    private String taskType;
    private String taskContent;
    private Timestamp publishTime;
    private Timestamp deadline;
    private String completionStatus;
    private Timestamp submitTime;
    private Integer score;
    private String gradeComment;
    private MultipartFile file;  // 新增字段
    private MultipartFile attachmentFile; // ⬅️ 新增字段：用于上传的文件对象
    private String studentName;
}
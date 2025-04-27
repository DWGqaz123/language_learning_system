package com.hdu.language_learning_system.exam.dto;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class UnGradedExamRecordDTO {
    private Integer studentId;
    private String studentName;
    private Integer examId;
    private String examName;
    private Timestamp examTime;
    private String answersJson;     // 学员作答内容
    private String paperContentJson; // 试卷内容
}
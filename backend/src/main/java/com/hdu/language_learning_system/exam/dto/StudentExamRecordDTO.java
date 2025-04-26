package com.hdu.language_learning_system.exam.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Data
public class StudentExamRecordDTO {
    private Integer studentId;
    private Integer examId;
    private Integer totalScore;
    private Integer subjectiveScore;
    private String teacherComment;
    private String assistantComment;
    private Timestamp completedTime;
    private String examName;
    private Timestamp examTime;
    private String answers;
    private String examStatus;
}
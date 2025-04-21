package com.hdu.language_learning_system.exam.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class StudentExamRecordDTO {
    private Integer studentId;
    private Integer examId;
    private Integer totalScore;
    private Integer subjectiveScore;
    private String teacherComment;
    private String assistantComment;
    private Timestamp completedTime;
}
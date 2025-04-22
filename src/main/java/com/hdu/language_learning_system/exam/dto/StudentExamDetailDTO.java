package com.hdu.language_learning_system.exam.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class StudentExamDetailDTO {
    private Integer examId;
    private Integer studentId;
    private String examName;
    private String examType;
    private Timestamp examTime;

    private String answersJson; // 学员答题内容
    private Integer objectiveScore;
    private Integer subjectiveScore;
    private Integer totalScore;
    private String teacherComment;
    private String assistantComment;
    private Timestamp completedTime;
}
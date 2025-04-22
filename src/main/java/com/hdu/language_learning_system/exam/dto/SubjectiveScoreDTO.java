package com.hdu.language_learning_system.exam.dto;

import lombok.Data;

@Data
public class SubjectiveScoreDTO {
    private Integer examId;
    private Integer studentId;
    private Integer subjectiveScore;
    private String teacherComment;

}
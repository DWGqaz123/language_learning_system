package com.hdu.language_learning_system.exam.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class StandardExamPaperDTO {
    private Integer paperId;
    private String paperName;
    private String examType;
    private String paperContentJson;
    private String objectiveAnswersJson;
    private Timestamp createdTime;
}
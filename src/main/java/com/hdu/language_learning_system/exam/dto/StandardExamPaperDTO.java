package com.hdu.language_learning_system.exam.dto;

import lombok.Data;

@Data
public class StandardExamPaperDTO {
    private Integer paperId;
    private String paperName;
    private String examType;
    private String paperContentJson;
    private String objectiveAnswersJson;
    private String createdTime;
}
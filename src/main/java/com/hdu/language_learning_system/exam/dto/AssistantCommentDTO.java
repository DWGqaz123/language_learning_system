package com.hdu.language_learning_system.exam.dto;

import lombok.Data;

@Data
public class AssistantCommentDTO {
    private Integer examId;
    private Integer studentId;
    private String assistantComment;
}
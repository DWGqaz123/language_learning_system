package com.hdu.language_learning_system.exam.dto;

import lombok.Data;

@Data
public class ExamAnswerSubmitDTO {
    private Integer examId;
    private Integer studentId;
    private String answersJson; // JSON 格式的答题内容
}
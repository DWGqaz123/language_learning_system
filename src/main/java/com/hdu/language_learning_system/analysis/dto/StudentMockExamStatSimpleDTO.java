package com.hdu.language_learning_system.analysis.dto;

import lombok.Data;

@Data
public class StudentMockExamStatSimpleDTO {
    private Integer studentId;
    private Integer examCount;
    private Double averageScore;
}
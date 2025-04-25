package com.hdu.language_learning_system.analysis.dto;

import lombok.Data;

@Data
public class GenerateReportRequestDTO {
    private Integer studentId;
    private Integer overallScore; // 由助教给出
}
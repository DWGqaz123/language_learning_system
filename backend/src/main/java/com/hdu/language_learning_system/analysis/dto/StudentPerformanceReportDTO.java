package com.hdu.language_learning_system.analysis.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class StudentPerformanceReportDTO {
    private Integer reportId;
    private Integer studentId;
    private String attendanceSummary;
    private String taskSummary;
    private String examSummary;
    private String studyRoomSummary;
    private Integer overallScore;
    private String assistantComment;
    private Timestamp generatedTime;
}
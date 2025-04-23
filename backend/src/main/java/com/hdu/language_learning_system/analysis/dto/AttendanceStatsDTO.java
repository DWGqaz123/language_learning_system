package com.hdu.language_learning_system.analysis.dto;

import lombok.Data;

@Data
public class AttendanceStatsDTO {
    private Integer studentId;
    private int totalRecords;
    private int attendCount;
    private int absentCount;
    private int leaveCount;
}
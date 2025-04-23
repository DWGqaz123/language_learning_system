package com.hdu.language_learning_system.course.dto;

import lombok.Data;

@Data
public class ScheduleStudentStatusDTO {
    private Integer studentId;
    private String studentName;
    private String attendStatus;
    private String leaveReason;
    private String performanceEval;
}
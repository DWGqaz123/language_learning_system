package com.hdu.language_learning_system.course.dto;

import lombok.Data;

@Data
public class StudentScheduleRecordDTO {
    private Integer scheduleId;
    private Integer studentId;
    private String attendStatus;
    private String leaveReason;
    private String performanceEval;
}

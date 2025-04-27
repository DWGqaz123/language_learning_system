package com.hdu.language_learning_system.course.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class StudentScheduleRecordDTO {
    private Integer scheduleId;
    private Integer studentId;
    private Integer ssrId;
    private String attendStatus;
    private String leaveReason;
    private String performanceEval;
    private String courseName;
    private String studentName;
    private Timestamp classTime;
}

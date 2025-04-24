package com.hdu.language_learning_system.course.dto;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class LeaveRequestDTO {
    private Integer studentId;
    private Integer scheduleId;
    private String leaveReason;
    private Integer ssrId;
    private String studentName;
    private Integer courseId;
    private String courseName;
    private Timestamp classTime;
    private String attendStatus;
}

package com.hdu.language_learning_system.course.dto;

import lombok.Data;

@Data
public class LeaveRequestDTO {
    private Integer studentId;
    private Integer scheduleId;
    private String leaveReason;
}

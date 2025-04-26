package com.hdu.language_learning_system.course.dto;

import lombok.Data;

@Data
public class StudentScheduleRecordSimpleDTO {
    private Integer ssrId;
    private Integer courseId;
    private String courseName;
    private Integer scheduleId;
    private String classTime;
}
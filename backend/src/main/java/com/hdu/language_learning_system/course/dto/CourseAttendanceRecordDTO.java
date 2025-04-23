package com.hdu.language_learning_system.course.dto;

import lombok.Data;

@Data
public class CourseAttendanceRecordDTO {
    private Integer courseId;
    private Long total;
    private Long attendCount;
    private Long absentCount;
    private Long leaveCount;
}
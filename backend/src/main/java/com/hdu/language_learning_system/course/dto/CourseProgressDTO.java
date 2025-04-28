package com.hdu.language_learning_system.course.dto;
import lombok.Data;

@Data
public class CourseProgressDTO {
    private Integer studentId;
    private Integer totalHours;
    private Integer remainingHours;
    private Integer completedHours; // ✅ 新增字段
    private Integer completedPercentage;
}
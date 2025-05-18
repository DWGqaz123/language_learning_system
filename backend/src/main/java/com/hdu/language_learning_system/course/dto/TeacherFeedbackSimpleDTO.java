package com.hdu.language_learning_system.course.dto;

import lombok.Data;

@Data
public class TeacherFeedbackSimpleDTO {
    private Integer scheduleId;
    private Integer feedbackScore; // 对教师的评分（如 1~5）
    private Integer studentId;
}

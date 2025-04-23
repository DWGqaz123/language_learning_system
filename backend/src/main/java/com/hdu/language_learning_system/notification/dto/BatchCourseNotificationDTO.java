package com.hdu.language_learning_system.notification.dto;

import lombok.Data;

@Data
public class BatchCourseNotificationDTO {
    private Integer courseId;             // 目标课程ID
    private Integer refTargetId;          // 引用的课表ID（可为 null）
    private String refTargetType;         // 如“课程通知”
    private String notificationType;      // 通知类型，如“提醒”、“通知”
    private String content;               // 通知内容
}

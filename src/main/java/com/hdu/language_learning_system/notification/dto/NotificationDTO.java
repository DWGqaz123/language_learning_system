package com.hdu.language_learning_system.notification.dto;

import lombok.Data;

@Data
public class NotificationDTO {
    private Integer receiverId;         // 接收人
    private String notificationType;    // 通知类型 如 课堂通知、考试通知、任务通知
    private Integer refTargetId;        // 引用对象ID（如schedule_id）
    private String refTargetType;       // 引用对象类型（如“课程安排表”）
    private String content;             // 通知内容
}
package com.hdu.language_learning_system.notification.dto;

import lombok.Data;

@Data
public class NotificationQueryDTO {
    private Integer receiverId;            // 必填：接收人 ID
    private String notificationType;       // 可选：筛选通知类型
    private Integer page = 0;              // 可选：分页页码，默认 0
    private Integer size = 10;             // 可选：分页大小，默认 10
}

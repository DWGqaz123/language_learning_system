package com.hdu.language_learning_system.notification.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.hdu.language_learning_system.user.entity.User;

import java.sql.Timestamp;

@Entity
@Table(name = "notification")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationId;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    private String notificationType;  // 例如：CLASS_NOTICE
    private Integer refTargetId;      // 关联的 schedule_id
    private String refTargetType;     // 一般为 "schedule"
    private String content;
    private Timestamp sentTime;
    private String status;            // 如 "未读"、"已读"
}
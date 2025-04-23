package com.hdu.language_learning_system.notification.entity;

import com.hdu.language_learning_system.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;

    // 接收人
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(name = "notification_type")
    private String notificationType;

    @Column(name = "ref_target_id")
    private Integer refTargetId;

    @Column(name = "ref_target_type")
    private String refTargetType;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "sent_time")
    private Timestamp sentTime;

    private String status;
    public Notification(Notification other) {
        this.notificationType = other.notificationType;
        this.refTargetId = other.refTargetId;
        this.refTargetType = other.refTargetType;
        this.content = other.content;
        this.sentTime = other.sentTime;
        this.status = other.status;
    }
    public Notification() {} // 保留默认构造器

}


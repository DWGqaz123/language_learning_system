package com.hdu.language_learning_system.notification.service;

import com.hdu.language_learning_system.notification.dto.NotificationDTO;
import com.hdu.language_learning_system.notification.dto.BatchCourseNotificationDTO;
import com.hdu.language_learning_system.notification.dto.NotificationQueryDTO;
import com.hdu.language_learning_system.notification.entity.Notification;

import java.util.List;

public interface NotificationService {

    // 发送单个通知
    void sendNotification(NotificationDTO dto);

    // 批量发送课程通知（老师、助教、所有学生）
    void sendBatchCourseNotifications(BatchCourseNotificationDTO dto);

    //通知查看
    List<Notification> getNotificationsByReceiver(NotificationQueryDTO dto);

    //通知确认
    void markAsRead(Integer notificationId);

}

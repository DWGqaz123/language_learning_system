package com.hdu.language_learning_system.notification.repository;

import com.hdu.language_learning_system.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Page<Notification> findByReceiver_UserIdAndNotificationType(Integer receiverId, String notificationType, Pageable pageable);
    Page<Notification> findByReceiver_UserId(Integer receiverId, Pageable pageable);
}

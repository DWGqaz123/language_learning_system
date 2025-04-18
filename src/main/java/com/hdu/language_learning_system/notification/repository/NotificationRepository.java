package com.hdu.language_learning_system.notification.repository;

import com.hdu.language_learning_system.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}

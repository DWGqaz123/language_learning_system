package com.hdu.language_learning_system.notification.controller;

import com.hdu.language_learning_system.notification.dto.BatchCourseNotificationDTO;
import com.hdu.language_learning_system.notification.dto.ConfirmNotificationDTO;
import com.hdu.language_learning_system.notification.dto.NotificationDTO;
import com.hdu.language_learning_system.notification.dto.NotificationQueryDTO;
import com.hdu.language_learning_system.notification.entity.Notification;
import com.hdu.language_learning_system.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // 单条通知发送
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationDTO dto) {
        try {
            notificationService.sendNotification(dto);
            return ResponseEntity.ok("通知发送成功");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("通知发送失败: " + e.getMessage());
        }
    }

    // 批量发送给课程学员和教师
    @PostMapping("/send-batch")
    public ResponseEntity<String> sendBatchCourseNotifications(@RequestBody BatchCourseNotificationDTO dto) {
        try {
            notificationService.sendBatchCourseNotifications(dto);
            return ResponseEntity.ok("批量通知发送成功");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("批量通知发送失败: " + e.getMessage());
        }
    }

    // 通知查看：获取某个接收人的通知列表（支持分页、可选类型过滤）
    @PostMapping("/query")
    public ResponseEntity<List<Notification>> getNotificationsByReceiver(@RequestBody NotificationQueryDTO dto) {
        List<Notification> notifications = notificationService.getNotificationsByReceiver(dto);
        return ResponseEntity.ok(notifications);
    }

    //通知确认
    @PostMapping("/mark-as-read")
    public ResponseEntity<String> markAsRead(@RequestBody ConfirmNotificationDTO dto) {
        try {
            notificationService.markAsRead(dto.getNotificationId());
            return ResponseEntity.ok("通知状态已更新为已读");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("更新失败：" + e.getMessage());
        }
    }

}
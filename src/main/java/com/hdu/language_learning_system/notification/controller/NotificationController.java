package com.hdu.language_learning_system.notification.controller;

import com.hdu.language_learning_system.common.ApiResponse;
import com.hdu.language_learning_system.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // 接口：发送课堂通知
    @PostMapping("/send-class")
    public ResponseEntity<ApiResponse<Void>> sendClassNotification(@RequestParam Integer scheduleId) {
        notificationService.sendClassNotification(scheduleId);
        return ResponseEntity.ok(ApiResponse.success("通知发送成功", null));
    }
}
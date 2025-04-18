package com.hdu.language_learning_system.notification.service.impl;

import com.hdu.language_learning_system.course.entity.Schedule;
import com.hdu.language_learning_system.course.repository.ScheduleRepository;
import com.hdu.language_learning_system.notification.entity.Notification;
import com.hdu.language_learning_system.notification.repository.NotificationRepository;
import com.hdu.language_learning_system.notification.service.NotificationService;
import com.hdu.language_learning_system.user.entity.User;
import com.hdu.language_learning_system.user.repository.UserRepository;
import com.hdu.language_learning_system.course.repository.StudentScheduleRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final String NOTIFICATION_TYPE_CLASS = "课堂通知";

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentScheduleRecordRepository studentScheduleRecordRepository;

    @Override
    public void sendClassNotification(Integer scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("未找到对应课程安排"));

        // 教师通知
        Notification teacherNotice = new Notification();
        teacherNotice.setReceiver(schedule.getTeacher());
        teacherNotice.setNotificationType(NOTIFICATION_TYPE_CLASS);
        teacherNotice.setRefTargetId(scheduleId);
        teacherNotice.setRefTargetType("Schedule");
        teacherNotice.setContent("您将于 " + schedule.getClassTime() + " 上课，请准时到场！");
        teacherNotice.setSentTime(new Timestamp(System.currentTimeMillis()));
        teacherNotice.setStatus("未读");
        notificationRepository.save(teacherNotice);

        // 学员通知
        List<User> students = studentScheduleRecordRepository.findStudentsByScheduleId(scheduleId);
        for (User student : students) {
            Notification studentNotice = new Notification();
            studentNotice.setReceiver(student);
            studentNotice.setNotificationType(NOTIFICATION_TYPE_CLASS);
            studentNotice.setRefTargetId(scheduleId);
            studentNotice.setRefTargetType("Schedule");
            studentNotice.setContent("您将于 " + schedule.getClassTime() + " 上课，请准时到场！");
            studentNotice.setSentTime(new Timestamp(System.currentTimeMillis()));
            studentNotice.setStatus("未读");
            notificationRepository.save(studentNotice);
        }
    }
}

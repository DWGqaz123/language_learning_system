package com.hdu.language_learning_system.notification.service.impl;

import com.hdu.language_learning_system.course.entity.ClassStudent;
import com.hdu.language_learning_system.course.entity.Course;
import com.hdu.language_learning_system.course.entity.Schedule;
import com.hdu.language_learning_system.course.entity.StudentScheduleRecord;
import com.hdu.language_learning_system.course.repository.ClassStudentRepository;
import com.hdu.language_learning_system.course.repository.CourseRepository;
import com.hdu.language_learning_system.course.repository.ScheduleRepository;
import com.hdu.language_learning_system.course.repository.StudentScheduleRecordRepository;
import com.hdu.language_learning_system.notification.dto.BatchCourseNotificationDTO;
import com.hdu.language_learning_system.notification.dto.NotificationDTO;
import com.hdu.language_learning_system.notification.dto.NotificationQueryDTO;
import com.hdu.language_learning_system.notification.entity.Notification;
import com.hdu.language_learning_system.notification.repository.NotificationRepository;
import com.hdu.language_learning_system.notification.service.NotificationService;
import com.hdu.language_learning_system.user.entity.User;
import com.hdu.language_learning_system.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ScheduleRepository scheduleRepository;
    private final ClassStudentRepository classStudentRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   CourseRepository courseRepository,
                                   ScheduleRepository scheduleRepository,
                                   ClassStudentRepository classStudentRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.scheduleRepository = scheduleRepository;
        this.classStudentRepository = classStudentRepository;
    }

    // 普通通知发送
    @Override
    public void sendNotification(NotificationDTO dto) {
        User receiver = userRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new RuntimeException("接收人不存在"));

        Notification notification = new Notification();
        notification.setReceiver(receiver);
        notification.setNotificationType(dto.getNotificationType());
        notification.setRefTargetId(dto.getRefTargetId());
        notification.setRefTargetType(dto.getRefTargetType());
        notification.setContent(dto.getContent());
        notification.setSentTime(new Timestamp(System.currentTimeMillis()));
        notification.setStatus("未读");

        notificationRepository.save(notification);
    }

    // 批量发送通知给班级学员、教师和助教
    @Override
    @Transactional
    public void sendBatchCourseNotifications(BatchCourseNotificationDTO dto) {
        Set<Integer> userIds = new HashSet<>();

        // 添加班级课程下的所有学员ID（使用 class_student 表）
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        if ("班级".equals(course.getCourseType())) {
            List<ClassStudent> classStudents = classStudentRepository.findByCourse_CourseId(dto.getCourseId());
            for (ClassStudent cs : classStudents) {
                userIds.add(cs.getStudent().getUserId());
            }
        }

        // 加入教师、助教、1对1学员
        if (dto.getRefTargetId() != null) {
            Schedule schedule = scheduleRepository.findById(dto.getRefTargetId())
                    .orElseThrow(() -> new RuntimeException("课表不存在"));

            if (schedule.getTeacher() != null) {
                userIds.add(schedule.getTeacher().getUserId());
            }

            if (schedule.getAssistant() != null) {
                userIds.add(schedule.getAssistant().getUserId());
            }

            if ("1对1".equals(course.getCourseType()) && course.getStudent() != null) {
                userIds.add(course.getStudent().getUserId());
            }
        }

        // 发送通知
        for (Integer userId : userIds) {
            User receiver = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("接收人不存在"));

            Notification notification = new Notification();
            notification.setReceiver(receiver);
            notification.setNotificationType(dto.getNotificationType());
            notification.setContent(dto.getContent());
            notification.setRefTargetId(dto.getRefTargetId());
            notification.setRefTargetType(dto.getRefTargetType());
            notification.setSentTime(new Timestamp(System.currentTimeMillis()));
            notification.setStatus("未读");

            notificationRepository.save(notification);
        }
    }

    //通知查看
    @Override
    public List<Notification> getNotificationsByReceiver(NotificationQueryDTO dto) {
        Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Sort.Direction.DESC, "sentTime"));

        if (dto.getNotificationType() != null && !dto.getNotificationType().isEmpty()) {
            return notificationRepository
                    .findByReceiver_UserIdAndNotificationType(dto.getReceiverId(), dto.getNotificationType(), pageable)
                    .getContent();
        } else {
            return notificationRepository
                    .findByReceiver_UserId(dto.getReceiverId(), pageable)
                    .getContent();
        }
    }

    //通知确认
    @Override
    @Transactional
    public void markAsRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("通知不存在"));

        notification.setStatus("已读");
        notificationRepository.save(notification);
    }

}
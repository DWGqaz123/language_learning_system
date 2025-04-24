package com.hdu.language_learning_system.course.service.impl;

import com.hdu.language_learning_system.course.dto.ScheduleCreateDTO;
import com.hdu.language_learning_system.course.dto.ScheduleUpdateDTO;
import com.hdu.language_learning_system.course.entity.*;
import com.hdu.language_learning_system.course.repository.*;
import com.hdu.language_learning_system.course.service.CourseService;
import com.hdu.language_learning_system.exam.entity.MockExam;
import com.hdu.language_learning_system.exam.repository.MockExamRepository;
import com.hdu.language_learning_system.studyRoom.entity.StudyRoom;
import com.hdu.language_learning_system.studyRoom.repository.StudyRoomRepository;
import com.hdu.language_learning_system.user.entity.User;
import com.hdu.language_learning_system.user.repository.UserRepository;
import com.hdu.language_learning_system.course.service.ScheduleService;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyRoomRepository studyRoomRepository;

    @Autowired
    private MockExamRepository mockExamRepository;
    @Resource
    private  StudentScheduleRecordRepository studentScheduleRecordRepository;

    @Autowired
    private CourseService courseService;
    private void checkRoomConflict(Integer roomId, Timestamp classTime, Integer excludeScheduleId) {
        // 1. 检查排课冲突（排除当前排课记录）
        List<Schedule> schedules = scheduleRepository.findByRoomIdAndDate(roomId, classTime);
        if (excludeScheduleId != null) {
            schedules.removeIf(s -> s.getScheduleId().equals(excludeScheduleId));
        }
        if (!schedules.isEmpty()) {
            throw new RuntimeException("该教室在该时间已有排课安排，请选择其他教室");
        }

        // 2. 检查模拟考试冲突
        List<MockExam> exams = mockExamRepository.findByRoomIdAndDate(roomId, classTime);
        if (!exams.isEmpty()) {
            throw new RuntimeException("该教室在该时间已有模拟考试安排，请选择其他教室");
        }
    }
    // 创建课表
    @Override
    @Transactional
    public void createSchedule(ScheduleCreateDTO dto) {
        Schedule schedule = new Schedule();

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        User teacher = userRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new RuntimeException("教师不存在"));

        User assistant = userRepository.findById(dto.getAssistantId())
                .orElse(null); // 助教可为空

        StudyRoom room = studyRoomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("教室不存在"));

        Timestamp classTime = dto.getClassTime();

        // 教室冲突检查
        checkRoomConflict(room.getRoomId(), classTime, null);

        // 设置课程基本信息
        schedule.setCourse(course);
        schedule.setTeacher(teacher);
        schedule.setAssistant(assistant);
        schedule.setRoom(room);
        schedule.setClassTime(classTime);

        // 教师角色校验
        if (teacher.getRole().getRoleId() != 2) {
            throw new RuntimeException("指定的用户不是教师");
        }

        // 助教角色校验
        if (assistant != null && assistant.getRole().getRoleId() != 3) {
            throw new RuntimeException("指定的用户不是助教");
        }

        // 扣除剩余课时
        int remain = course.getRemainingHours();
        if ("班级".equals(course.getCourseType())) {
            if (remain < 2) {
                throw new RuntimeException("课程剩余课时不足，无法创建班级课课表");
            }
            course.setRemainingHours(remain - 2);
        } else if ("1对1".equals(course.getCourseType())) {
            if (remain < 1) {
                throw new RuntimeException("课程剩余课时不足，无法创建1对1课课表");
            }
            course.setRemainingHours(remain - 1);
        }

        courseRepository.save(course);
        scheduleRepository.save(schedule);

        // 新增逻辑：生成学生课堂记录并发送通知
        courseService.generateStudentRecords(schedule.getScheduleId());
    }

    //更新排课

    @Override
    @Transactional
    public void updateSchedule(ScheduleUpdateDTO dto) {
        Schedule schedule = scheduleRepository.findById(dto.getScheduleId())
                .orElseThrow(() -> new RuntimeException("排课记录不存在"));

        // 教室和时间变更时进行冲突检查
        if (dto.getRoomId() != null && dto.getClassTime() != null) {
            checkRoomConflict(dto.getRoomId(), dto.getClassTime(), schedule.getScheduleId());

            StudyRoom room = studyRoomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new RuntimeException("教室不存在"));
            schedule.setRoom(room);
            schedule.setClassTime(dto.getClassTime());
        }

        // 教师变更
        if (dto.getTeacherId() != null) {
            User teacher = userRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("教师不存在"));
            schedule.setTeacher(teacher);
        }

        // 助教变更
        if (dto.getAssistantId() != null) {
            User assistant = userRepository.findById(dto.getAssistantId())
                    .orElseThrow(() -> new RuntimeException("助教不存在"));
            schedule.setAssistant(assistant);
        }

        scheduleRepository.save(schedule);

        // 更新后的课表，也应生成对应课堂记录
        courseService.generateStudentRecords(schedule.getScheduleId());
    }

    //删除课表
    @Override
    @Transactional
    public void deleteSchedule(Integer scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("排课记录不存在"));

        // 删除与 schedule 关联的 student_schedule_records 记录
        List<StudentScheduleRecord> records = studentScheduleRecordRepository.findBySchedule_ScheduleId(scheduleId);
        studentScheduleRecordRepository.deleteAll(records);

        // 删除排课记录
        scheduleRepository.delete(schedule);
    }

}
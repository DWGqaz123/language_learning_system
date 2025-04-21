package com.hdu.language_learning_system.course.service.impl;

import com.hdu.language_learning_system.course.dto.ScheduleCreateDTO;
import com.hdu.language_learning_system.course.dto.ScheduleUpdateDTO;
import com.hdu.language_learning_system.course.entity.*;
import com.hdu.language_learning_system.course.repository.*;
import com.hdu.language_learning_system.studyRoom.entity.StudyRoom;
import com.hdu.language_learning_system.studyRoom.repository.StudyRoomRepository;
import com.hdu.language_learning_system.user.entity.User;
import com.hdu.language_learning_system.user.repository.UserRepository;
import com.hdu.language_learning_system.course.service.ScheduleService;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Resource
    private  StudentScheduleRecordRepository studentScheduleRecordRepository;

    @Override
    public void createSchedule(ScheduleCreateDTO dto) {
        Schedule schedule = new Schedule();

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        User teacher = userRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new RuntimeException("教师不存在"));

        User assistant = userRepository.findById(dto.getAssistantId())
                .orElse(null); // 允许为空

        StudyRoom room = studyRoomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("教室不存在"));

        schedule.setCourse(course);
        schedule.setTeacher(teacher);
        schedule.setAssistant(assistant);
        schedule.setRoom(room);
        schedule.setClassTime(dto.getClassTime());

        // 检查教师角色是否为“教师”
        if (teacher.getRole().getRoleId() != 2) {
            throw new RuntimeException("指定的用户不是教师");
        }

        // 检查助教角色是否为“助教”
        if (assistant.getRole().getRoleId() != 3) {
            throw new RuntimeException("指定的用户不是助教");
        }

        scheduleRepository.save(schedule);
    }

    //更新排课

    @Transactional
    @Override
    public void updateSchedule(ScheduleUpdateDTO dto) {
        Schedule schedule = scheduleRepository.findById(dto.getScheduleId())
                .orElseThrow(() -> new RuntimeException("排课记录不存在"));

        if (dto.getTeacherId() != null) {
            User teacher = userRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("教师不存在"));
            schedule.setTeacher(teacher);
        }

        if (dto.getAssistantId() != null) {
            User assistant = userRepository.findById(dto.getAssistantId())
                    .orElseThrow(() -> new RuntimeException("助教不存在"));
            schedule.setAssistant(assistant);
        }

        if (dto.getRoomId() != null) {
            StudyRoom room = studyRoomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new RuntimeException("教室不存在"));
            schedule.setRoom(room);
        }

        if (dto.getClassTime() != null) {
            schedule.setClassTime(dto.getClassTime());
        }

        scheduleRepository.save(schedule);
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
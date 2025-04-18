package com.hdu.language_learning_system.course.service.impl;

import com.hdu.language_learning_system.course.dto.ScheduleCreateDTO;
import com.hdu.language_learning_system.course.entity.Course;
import com.hdu.language_learning_system.course.entity.Schedule;
import com.hdu.language_learning_system.course.repository.CourseRepository;
import com.hdu.language_learning_system.course.repository.ScheduleRepository;
import com.hdu.language_learning_system.studyRoom.entity.StudyRoom;
import com.hdu.language_learning_system.studyRoom.repository.StudyRoomRepository;
import com.hdu.language_learning_system.user.entity.User;
import com.hdu.language_learning_system.user.repository.UserRepository;
import com.hdu.language_learning_system.course.service.ScheduleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
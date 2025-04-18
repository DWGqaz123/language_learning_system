package com.hdu.language_learning_system.course.service.impl;

import com.hdu.language_learning_system.course.dto.CourseCreateDTO;
import com.hdu.language_learning_system.course.dto.AddStudentsToCourseDTO;
import com.hdu.language_learning_system.course.entity.Course;
import com.hdu.language_learning_system.course.entity.Schedule;
import com.hdu.language_learning_system.course.entity.StudentScheduleRecord;
import com.hdu.language_learning_system.course.repository.CourseRepository;
import com.hdu.language_learning_system.course.repository.ScheduleRepository;
import com.hdu.language_learning_system.course.repository.StudentScheduleRecordRepository;
import com.hdu.language_learning_system.course.service.CourseService;
import com.hdu.language_learning_system.user.entity.User;
import com.hdu.language_learning_system.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final StudentScheduleRecordRepository studentScheduleRecordRepository;

    @Autowired
    public CourseServiceImpl(
            CourseRepository courseRepository,
            UserRepository userRepository,
            ScheduleRepository scheduleRepository,
            StudentScheduleRecordRepository studentScheduleRecordRepository
    ) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.studentScheduleRecordRepository = studentScheduleRecordRepository;
    }

    @Override
    public void createCourse(CourseCreateDTO dto) {
        Course course = new Course();
        course.setCourseName(dto.getCourseName());
        course.setCourseType(dto.getCourseType());
        course.setCourseContent(dto.getCourseContent());
        course.setClassGroupCode(dto.getClassGroupCode());
        course.setTotalHours(dto.getTotalHours());

        if ("1对1".equals(dto.getCourseType()) && dto.getStudentId() != null) {
            User student = userRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new RuntimeException("学员不存在"));
            course.setStudent(student);
        } else {
            course.setStudent(null);
        }

        if ("1对1".equals(course.getCourseType())) {
            User student = userRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new RuntimeException("学员不存在"));

            int currentHours = student.getLessonHours();
            if (currentHours < course.getTotalHours()) {
                throw new RuntimeException("学员课时不足");
            }

            student.setLessonHours(currentHours - course.getTotalHours());
            userRepository.save(student);
        }

        courseRepository.save(course);
    }

    @Override
    public void addStudentsToClassCourse(AddStudentsToCourseDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        if (!"班级".equals(course.getCourseType())) {
            throw new RuntimeException("该操作仅适用于班级课程");
        }

        for (Integer studentId : dto.getStudentIds()) {
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("学员不存在: " + studentId));

            StudentScheduleRecord record = new StudentScheduleRecord();
            record.setStudent(student);
            record.setCourse(course);
            record.setSchedule(null); // 暂不绑定课表
            record.setJoinTime(new Timestamp(System.currentTimeMillis()));
            record.setAttendStatus("未开始");
            studentScheduleRecordRepository.save(record);
        }
    }

    @Override
    public void generateStudentRecords(Integer scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("课表不存在"));

        Course course = schedule.getCourse();
        String courseType = course.getCourseType();

        if ("1对1".equals(courseType)) {
            User student = course.getStudent();
            if (student == null) throw new RuntimeException("1对1课程未绑定学员");

            StudentScheduleRecord record = new StudentScheduleRecord();
            record.setSchedule(schedule);
            record.setCourse(course);
            record.setStudent(student);
            record.setJoinTime(new Timestamp(System.currentTimeMillis()));
            record.setAttendStatus("未开始");
            studentScheduleRecordRepository.save(record);
        } else {
            List<StudentScheduleRecord> records =
                    studentScheduleRecordRepository.findAllByScheduleIsNullAndCourse_CourseId(course.getCourseId());
            for (StudentScheduleRecord record : records) {
                record.setSchedule(schedule);
                record.setJoinTime(new Timestamp(System.currentTimeMillis()));
                record.setAttendStatus("未开始");
                studentScheduleRecordRepository.save(record);
                studentScheduleRecordRepository.save(record);
                studentScheduleRecordRepository.flush(); // 强制刷新
                System.out.println("即将保存记录: 学生ID=" + record.getStudent().getUserId()
                        + ", schedule=" + (record.getSchedule() != null ? record.getSchedule().getScheduleId() : "null"));
            }
        }

    }
}
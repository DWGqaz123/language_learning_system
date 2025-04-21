package com.hdu.language_learning_system.course.service.impl;

import com.hdu.language_learning_system.course.dto.*;
import com.hdu.language_learning_system.course.entity.Course;
import com.hdu.language_learning_system.course.entity.Schedule;
import com.hdu.language_learning_system.course.entity.StudentScheduleRecord;
import com.hdu.language_learning_system.course.repository.CourseRepository;
import com.hdu.language_learning_system.course.repository.ScheduleRepository;
import com.hdu.language_learning_system.course.repository.StudentScheduleRecordRepository;
import com.hdu.language_learning_system.course.service.CourseService;
import com.hdu.language_learning_system.user.entity.User;
import com.hdu.language_learning_system.user.repository.UserRepository;
import com.hdu.language_learning_system.course.dto.PerformanceEvalDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    //创建课程
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

    //添加班级学员
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
            // 添加显式刷新
            studentScheduleRecordRepository.flush();
        }
    }

    //产生课堂学员记录
    @Override
    public void generateStudentRecords(Integer scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("课表不存在"));

        // 确保Schedule对象已完全持久化
        scheduleRepository.flush();

        Course course = schedule.getCourse();

        if ("1对1".equals(course.getCourseType())) {
            // 现有1对1处理代码...
            studentScheduleRecordRepository.flush(); // 添加此行
        } else {
            List<StudentScheduleRecord> records =
                    studentScheduleRecordRepository.findNullScheduleRecordsByCourseIdNative(course.getCourseId());

            System.out.println("找到" + records.size() + "条需要关联的记录");

            for (StudentScheduleRecord record : records) {
                record.setSchedule(schedule);
                record.setJoinTime(new Timestamp(System.currentTimeMillis()));
                record.setAttendStatus("未开始");
                studentScheduleRecordRepository.save(record);
            }

            // 显式刷新，确保立即写入数据库
            studentScheduleRecordRepository.flush();
        }
    }

    // 学员请假
    @Override
    @Transactional
    public void submitLeaveRequest(LeaveRequestDTO dto) {
        StudentScheduleRecord record = studentScheduleRecordRepository
                .findByStudentIdAndScheduleId(dto.getStudentId(), dto.getScheduleId())
                .orElseThrow(() -> new RuntimeException("未找到该学员的课程记录"));

        record.setAttendStatus("请假");
        record.setLeaveReason(dto.getLeaveReason());
        studentScheduleRecordRepository.save(record);
    }

    //课程考勤统计查看
    @Override
    public CourseAttendanceRecordDTO getAttendanceStatsByCourseId(Integer courseId) {
        CourseAttendanceRecordDTO dto = new CourseAttendanceRecordDTO();
        dto.setCourseId(courseId);
        dto.setTotal(studentScheduleRecordRepository.countTotalByCourseId(courseId));
        dto.setAttendCount(studentScheduleRecordRepository.countAttendByCourseId(courseId));
        dto.setAbsentCount(studentScheduleRecordRepository.countAbsentByCourseId(courseId));
        dto.setLeaveCount(studentScheduleRecordRepository.countLeaveByCourseId(courseId));
        return dto;
    }
    //课堂考勤
    @Override
    @Transactional
    public void updateAttendanceStatus(AttendanceStatusDTO dto) {
        StudentScheduleRecord record = studentScheduleRecordRepository
                .findByScheduleIdAndStudentId(dto.getScheduleId(), dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("未找到该学生的排课记录"));

        record.setAttendStatus(dto.getAttendStatus());
        studentScheduleRecordRepository.save(record);
    }

    //教师评价学员表现
    @Override
    @Transactional
    public void updatePerformanceEval(PerformanceEvalDTO dto) {
        StudentScheduleRecord record = studentScheduleRecordRepository
                .findBySchedule_ScheduleIdAndStudent_UserId(dto.getScheduleId(), dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("未找到对应的学生课程记录"));

        record.setPerformanceEval(dto.getPerformanceEval());
        studentScheduleRecordRepository.save(record);
    }

    //查询课程课表
    @Override
    public List<CourseScheduleDTO> getScheduleByCourseId(Integer courseId) {
        List<Schedule> schedules = scheduleRepository.findByCourse_CourseId(courseId);
        List<CourseScheduleDTO> result = new ArrayList<>();

        for (Schedule s : schedules) {
            CourseScheduleDTO dto = new CourseScheduleDTO();
            dto.setScheduleId(s.getScheduleId());
            dto.setClassTime(s.getClassTime());

            // 打印 room 是否为空
            if (s.getRoom() == null) {
                System.out.println("room is null for scheduleId: " + s.getScheduleId());
            } else {
                System.out.println("room is NOT null, room name: " + s.getRoom().getRoomName());
            }

            dto.setRoomName(s.getRoom() != null ? s.getRoom().getRoomName() : null);

            if (s.getTeacher() != null) {
                dto.setTeacherId(s.getTeacher().getUserId());
                dto.setTeacherName(s.getTeacher().getUsername());
            }
            if (s.getAssistant() != null) {
                dto.setAssistantId(s.getAssistant().getUserId());
                dto.setAssistantName(s.getAssistant().getUsername());
            }

            result.add(dto);
        }
        return result;
    }

    //分角色查看课表
    @Override
    public List<CourseScheduleDTO> getSchedulesByUserId(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow();
        String role = user.getRole().getRoleName();
        List<Schedule> schedules;

        switch (role) {
            case "教师" -> schedules = scheduleRepository.findByTeacher_UserId(userId);
            case "助教" -> schedules = scheduleRepository.findByAssistant_UserId(userId);
            case "学员" -> schedules = scheduleRepository.findSchedulesByStudentId(userId);
            default -> throw new RuntimeException("无效角色");
        }

        List<CourseScheduleDTO> result = new ArrayList<>();
        for (Schedule s : schedules) {
            CourseScheduleDTO dto = new CourseScheduleDTO();
            dto.setScheduleId(s.getScheduleId());
            dto.setClassTime(s.getClassTime());
            dto.setCourseName(s.getCourse().getCourseName());

            if (s.getTeacher() != null) {
                dto.setTeacherId(s.getTeacher().getUserId());
                dto.setTeacherName(s.getTeacher().getUsername());
            }
            if (s.getAssistant() != null) {
                dto.setAssistantId(s.getAssistant().getUserId());
                dto.setAssistantName(s.getAssistant().getUsername());
            }
            if (s.getRoom() != null) {
                dto.setRoomName(s.getRoom().getRoomName());
            }
            result.add(dto);
        }
        return result;
    }

    //查看某次课的考勤

    @Override
    public List<ScheduleStudentStatusDTO> getStudentStatusByScheduleId(Integer scheduleId) {
        List<StudentScheduleRecord> records = studentScheduleRecordRepository.findByScheduleId(scheduleId);
        List<ScheduleStudentStatusDTO> result = new ArrayList<>();
        for (StudentScheduleRecord record : records) {
            ScheduleStudentStatusDTO dto = new ScheduleStudentStatusDTO();
            dto.setStudentId(record.getStudent().getUserId());
            dto.setStudentName(record.getStudent().getUsername());
            dto.setAttendStatus(record.getAttendStatus());
            dto.setLeaveReason(record.getLeaveReason());
            dto.setPerformanceEval(record.getPerformanceEval());
            result.add(dto);
        }
        return result;
    }

    //学员查看自己课堂表现
    @Override
    public List<StudentAttendancePerformanceDTO> getStudentAttendancePerformance(Integer studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!"学员".equals(user.getRole().getRoleName())) {
            throw new RuntimeException("只有学员才能查看自己的出勤与课堂表现");
        }

        List<StudentScheduleRecord> records = studentScheduleRecordRepository.findByStudentId(studentId);
        List<StudentAttendancePerformanceDTO> result = new ArrayList<>();

        for (StudentScheduleRecord r : records) {
            StudentAttendancePerformanceDTO dto = new StudentAttendancePerformanceDTO();
            dto.setScheduleId(r.getSchedule().getScheduleId());
            dto.setClassTime(r.getSchedule().getClassTime());
            dto.setCourseName(r.getCourse().getCourseName());
            dto.setAttendStatus(r.getAttendStatus());
            dto.setPerformanceEval(r.getPerformanceEval());
            result.add(dto);
        }
        return result;
    }

    //根据用户ID查询课程
    @Override
    public List<UserCourseDTO> getCoursesByUserId(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        String role = user.getRole().getRoleName();

        List<Course> courses = new ArrayList<>();

        switch (role) {
            case "教师" -> {
                List<Schedule> teacherSchedules = scheduleRepository.findByTeacher_UserId(userId);
                for (Schedule s : teacherSchedules) {
                    if (s.getCourse() != null) courses.add(s.getCourse());
                }
            }
            case "助教" -> {
                List<Schedule> assistantSchedules = scheduleRepository.findByAssistant_UserId(userId);
                for (Schedule s : assistantSchedules) {
                    if (s.getCourse() != null) courses.add(s.getCourse());
                }
            }
            case "学员" -> {
                List<StudentScheduleRecord> studentRecords = studentScheduleRecordRepository.findByStudent_UserId(userId);
                for (StudentScheduleRecord ssr : studentRecords) {
                    if (ssr.getCourse() != null) courses.add(ssr.getCourse());
                }
            }
            default -> throw new RuntimeException("无效用户角色");
        }

        // 去重 + 映射为DTO
        return courses.stream()
                .distinct()
                .map(c -> {
                    UserCourseDTO dto = new UserCourseDTO();
                    dto.setCourseId(c.getCourseId());
                    dto.setCourseName(c.getCourseName());
                    dto.setCourseType(c.getCourseType());
                    dto.setClassGroupCode(c.getClassGroupCode());
                    return dto;
                }).collect(Collectors.toList());
    }
    //学员请假审批
    @Override
    public void reviewLeaveRequest(LeaveReviewDTO dto) {
        StudentScheduleRecord record = studentScheduleRecordRepository.findById(dto.getSsrId())
                .orElseThrow(() -> new RuntimeException("未找到学生课程记录"));

        if ("请假".equals(record.getAttendStatus())) {
            if (Boolean.TRUE.equals(dto.getApproved())) {
                // 审核通过，维持“请假”状态
                record.setReviewComment(dto.getReviewComment());
            } else {
                // 审核不通过，设为“未开始”
                record.setAttendStatus("未开始");
                record.setReviewComment(dto.getReviewComment());
            }
            studentScheduleRecordRepository.save(record);
        } else {
            throw new RuntimeException("当前记录不处于请假状态，无法审核");
        }
    }

    //修改课程信息
    @Override
    public void updateCourse(CourseUpdateDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        course.setCourseName(dto.getCourseName());
        course.setCourseType(dto.getCourseType());
        course.setCourseContent(dto.getCourseContent());
        course.setClassGroupCode(dto.getClassGroupCode());

        if ("1对1".equals(dto.getCourseType())) {
            if (dto.getStudentId() != null) {
                User student = userRepository.findById(dto.getStudentId())
                        .orElseThrow(() -> new RuntimeException("学生不存在"));
                course.setStudent(student);
            }
            course.setTotalHours(dto.getTotalHours());
        } else {
            course.setStudent(null);
            course.setTotalHours(null);
        }

        courseRepository.save(course);
    }

    //删除课程
    @Override
    public void deleteCourse(Integer courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("课程不存在，无法删除");
        }
        courseRepository.deleteById(courseId);
    }

    //移除班级学员
    @Transactional
    @Override
    public void removeStudentFromCourse(Integer courseId, Integer studentId) {
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("课程不存在");
        }
        if (!userRepository.existsById(studentId)) {
            throw new RuntimeException("学员不存在");
        }

        studentScheduleRecordRepository.deleteByCourse_CourseIdAndStudent_UserId(courseId, studentId);
    }

    //查询排课详情
    // CourseServiceImpl.java
    @Override
    public CourseScheduleDetailDTO getScheduleDetailById(Integer scheduleId) {
        Schedule s = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("排课不存在"));

        CourseScheduleDetailDTO dto = new CourseScheduleDetailDTO();
        dto.setScheduleId(s.getScheduleId());
        dto.setClassTime(s.getClassTime());
        dto.setCourseName(s.getCourse().getCourseName());
        dto.setCourseType(s.getCourse().getCourseType());

        if (s.getTeacher() != null) {
            dto.setTeacherId(s.getTeacher().getUserId());
            dto.setTeacherName(s.getTeacher().getUsername());
        }
        if (s.getAssistant() != null) {
            dto.setAssistantId(s.getAssistant().getUserId());
            dto.setAssistantName(s.getAssistant().getUsername());
        }
        if (s.getRoom() != null) {
            dto.setRoomName(s.getRoom().getRoomName());
        }

        return dto;
    }

    //查询某课程某课表下的请假申请
    @Override
    public List<LeaveRequestDTO> getLeaveRequestsByCourseAndSchedule(Integer courseId, Integer scheduleId) {
        List<StudentScheduleRecord> records = studentScheduleRecordRepository
                .findByCourse_CourseIdAndSchedule_ScheduleIdAndAttendStatus(courseId, scheduleId, "请假");

        List<LeaveRequestDTO> result = new ArrayList<>();
        for (StudentScheduleRecord r : records) {
            LeaveRequestDTO dto = new LeaveRequestDTO();
            dto.setSsrId(r.getSsrId());
            dto.setStudentId(r.getStudent().getUserId());
            dto.setStudentName(r.getStudent().getUsername());
            dto.setCourseId(r.getCourse().getCourseId());
            dto.setCourseName(r.getCourse().getCourseName());
            dto.setScheduleId(r.getSchedule().getScheduleId());
            dto.setClassTime(r.getSchedule().getClassTime());
            dto.setLeaveReason(r.getLeaveReason());
            result.add(dto);
        }
        return result;
    }
}
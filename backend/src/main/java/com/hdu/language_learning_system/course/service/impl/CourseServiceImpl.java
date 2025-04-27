package com.hdu.language_learning_system.course.service.impl;

import com.hdu.language_learning_system.course.dto.*;
import com.hdu.language_learning_system.course.entity.ClassStudent;
import com.hdu.language_learning_system.course.entity.Course;
import com.hdu.language_learning_system.course.entity.Schedule;
import com.hdu.language_learning_system.course.entity.StudentScheduleRecord;
import com.hdu.language_learning_system.course.repository.ClassStudentRepository;
import com.hdu.language_learning_system.course.repository.CourseRepository;
import com.hdu.language_learning_system.course.repository.ScheduleRepository;
import com.hdu.language_learning_system.course.repository.StudentScheduleRecordRepository;
import com.hdu.language_learning_system.course.service.CourseService;
import com.hdu.language_learning_system.notification.dto.BatchCourseNotificationDTO;
import com.hdu.language_learning_system.user.entity.Role;
import com.hdu.language_learning_system.user.entity.User;
import com.hdu.language_learning_system.user.repository.UserRepository;
import com.hdu.language_learning_system.course.dto.PerformanceEvalDTO;
import com.hdu.language_learning_system.notification.service.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final StudentScheduleRecordRepository studentScheduleRecordRepository;

    @Autowired
    private ClassStudentRepository classStudentRepository;



    @Autowired
    private NotificationService notificationService;

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

    // 创建课程
    @Override
    public void createCourse(CourseCreateDTO dto) {
        Course course = new Course();
        course.setCourseName(dto.getCourseName());
        course.setCourseType(dto.getCourseType());
        course.setCourseContent(dto.getCourseContent());
        course.setClassGroupCode(dto.getClassGroupCode());
        course.setTotalHours(dto.getTotalHours());
        course.setRemainingHours(dto.getTotalHours()); // 设置初始剩余课时

        if ("1对1".equals(dto.getCourseType()) && dto.getStudentId() != null) {
            User student = userRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new RuntimeException("学员不存在"));

            int currentHours = student.getLessonHours();
            if (currentHours < dto.getTotalHours()) {
                throw new RuntimeException("学员课时不足");
            }

            // 扣除学员课时
            student.setLessonHours(currentHours - dto.getTotalHours());
            userRepository.save(student);

            course.setStudent(student);
        } else {
            course.setStudent(null);
        }

        courseRepository.save(course);
    }

    //添加班级学员
    @Override
    @Transactional
    public void addClassStudent(ClassStudentOperationDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        for (Integer studentId : dto.getStudentIds()) {
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("学生不存在，ID: " + studentId));

            boolean exists = classStudentRepository.existsByCourse_CourseIdAndStudent_UserId(course.getCourseId(), studentId);
            if (!exists) {
                ClassStudent cs = new ClassStudent();
                cs.setCourse(course);
                cs.setStudent(student);
                cs.setJoinTime(new Timestamp(System.currentTimeMillis()));
                classStudentRepository.save(cs);
            }
        }
    }

    //删除班级学员
    @Override
    @Transactional
    public void removeClassStudent(ClassStudentOperationDTO dto) {
        List<ClassStudent> records = classStudentRepository
                .findByCourse_CourseIdAndStudent_UserIdIn(dto.getCourseId(), dto.getStudentIds());

        if (records.isEmpty()) {
            throw new RuntimeException("所选学生均未在该课程中");
        }

        classStudentRepository.deleteAll(records);
    }
    //获取班级学员列表
    @Override
    public List<ClassStudentInfoDTO> getClassStudentList(Integer courseId) {
        List<ClassStudent> list = classStudentRepository.findByCourse_CourseId(courseId);

        return list.stream().map(cs -> {
            ClassStudentInfoDTO dto = new ClassStudentInfoDTO();
            dto.setStudentId(cs.getStudent().getUserId());
            dto.setUsername(cs.getStudent().getUsername());
            dto.setPhoneNumber(cs.getStudent().getPhoneNumber());
            return dto;
        }).toList();
    }

    //产生学员课堂记录
    @Override
    @Transactional
    public void generateStudentRecords(Integer scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("课表不存在"));

        Course course = schedule.getCourse();

        if ("1对1".equals(course.getCourseType())) {
            User student = course.getStudent();
            if (student == null) {
                throw new RuntimeException("该1对1课程未绑定学员");
            }

            // 为该 schedule 创建学员记录
            StudentScheduleRecord record = new StudentScheduleRecord();
            record.setCourse(course);
            record.setSchedule(schedule);
            record.setStudent(student);
            record.setJoinTime(new Timestamp(System.currentTimeMillis()));
            record.setAttendStatus("未开始");

            studentScheduleRecordRepository.save(record);
            studentScheduleRecordRepository.flush();

        } else {
            // 班级课：为该课程下所有学员 + 当前课表生成新记录
            List<User> students = courseRepository.findClassStudentsByCourseId(course.getCourseId());

            if (students.isEmpty()) {
                throw new RuntimeException("该班级课程下没有学员");
            }

            for (User student : students) {
                StudentScheduleRecord record = new StudentScheduleRecord();
                record.setCourse(course);
                record.setSchedule(schedule);
                record.setStudent(student);
                record.setJoinTime(new Timestamp(System.currentTimeMillis()));
                record.setAttendStatus("未开始");
                studentScheduleRecordRepository.save(record);
            }

            studentScheduleRecordRepository.flush();
        }

        // ---------- 批量发送课程通知 ----------
        BatchCourseNotificationDTO notify = new BatchCourseNotificationDTO();
        notify.setCourseId(course.getCourseId());
        notify.setRefTargetId(schedule.getScheduleId());
        notify.setRefTargetType("课表");
        notify.setNotificationType("课程通知");
        notify.setContent("你有新的上课安排，请及时查看");

        notificationService.sendBatchCourseNotifications(notify);
    }

    // 学员请假
    @Override
    @Transactional
    public void submitLeaveRequest(LeaveRequestDTO dto) {
        StudentScheduleRecord record = studentScheduleRecordRepository.findById(dto.getSsrId())
                .orElseThrow(() -> new RuntimeException("未找到该学员的课程记录"));

        if (!"未开始".equals(record.getAttendStatus())) {
            throw new RuntimeException("当前状态不允许申请请假");
        }

        record.setAttendStatus("请假待批");
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
            dto.setRoomId(s.getRoom().getRoomId());
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Role role = user.getRole();
        if (role == null || role.getRoleId() == null) {
            throw new RuntimeException("用户未分配角色");
        }

        int roleId = role.getRoleId();
        List<Schedule> schedules;

        switch (roleId) {
            case 2 -> schedules = scheduleRepository.findByTeacher_UserId(userId); // 教师
            case 3 -> schedules = scheduleRepository.findByAssistant_UserId(userId); // 助教
            case 1 -> schedules = scheduleRepository.findSchedulesByStudentId(userId); // 学员
            default -> throw new RuntimeException("无效角色ID：" + roleId);
        }

        List<CourseScheduleDTO> result = new ArrayList<>();
        for (Schedule s : schedules) {
            CourseScheduleDTO dto = new CourseScheduleDTO();
            dto.setScheduleId(s.getScheduleId());
            dto.setClassTime(s.getClassTime());
            dto.setCourseName(s.getCourse().getCourseName());
            dto.setCourseId(s.getCourse().getCourseId());

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

    // 根据用户ID查询课程
    @Override
    public List<UserCourseDTO> getCoursesByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Integer roleId = user.getRole().getRoleId();

        List<Course> courseList = new ArrayList<>();

        switch (roleId) {
            case 2 -> { // 教师
                List<Schedule> teacherSchedules = scheduleRepository.findByTeacher_UserId(userId);
                for (Schedule s : teacherSchedules) {
                    if (s.getCourse() != null) {
                        courseList.add(s.getCourse());
                    }
                }
            }
            case 3 -> { // 助教
                List<Schedule> assistantSchedules = scheduleRepository.findByAssistant_UserId(userId);
                for (Schedule s : assistantSchedules) {
                    if (s.getCourse() != null) {
                        courseList.add(s.getCourse());
                    }
                }
            }
            case 1 -> { // 学员
                List<ClassStudent> classStudents = classStudentRepository.findByStudent_UserId(userId);
                for (ClassStudent cs : classStudents) {
                    if (cs.getCourse() != null) {
                        courseList.add(cs.getCourse());
                    }
                }
                List<Course> oneOnOneCourses = courseRepository.findByStudent_UserId(userId);
                courseList.addAll(oneOnOneCourses);
            }
            default -> throw new RuntimeException("无效用户角色");
        }

        // 直接映射为DTO
        return courseList.stream()
                .filter(Objects::nonNull)
                .map(c -> {
                    UserCourseDTO dto = new UserCourseDTO();
                    dto.setCourseId(c.getCourseId());
                    dto.setCourseName(c.getCourseName());
                    dto.setCourseType(c.getCourseType());
                    dto.setClassGroupCode(c.getClassGroupCode());
                    dto.setTotalHours(c.getTotalHours());
                    dto.setRemainingHours(c.getRemainingHours());
                    return dto;
                })
                .distinct() // 防止重复
                .collect(Collectors.toList());
    }

    //学员请假审批
    @Override
    public void reviewLeaveRequest(LeaveReviewDTO dto) {
        StudentScheduleRecord record = studentScheduleRecordRepository.findById(dto.getSsrId())
                .orElseThrow(() -> new RuntimeException("未找到学生课程记录"));

        if ("请假待批".equals(record.getAttendStatus())) {
            if (Boolean.TRUE.equals(dto.getApproved())) {
                // 审核通过，设置为“请假”
                record.setAttendStatus("请假");
            } else {
                // 审核不通过，恢复为“未开始”
                record.setAttendStatus("未开始");
            }
            record.setReviewComment(dto.getReviewComment());
            studentScheduleRecordRepository.save(record);
        } else {
            throw new RuntimeException("当前记录不处于请假待批状态，无法审核");
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
        course.setTotalHours(dto.getTotalHours());

        if ("1对1".equals(dto.getCourseType())) {
            if (dto.getStudentId() != null) {
                User student = userRepository.findById(dto.getStudentId())
                        .orElseThrow(() -> new RuntimeException("学生不存在"));
                course.setStudent(student);
            }
        } else {
            course.setStudent(null);
        }

        courseRepository.save(course);
    }

    //删除课程
    @Override
    @Transactional
    public void deleteCourse(Integer courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在，无法删除"));

        // 删除该课程下的所有排课记录（Schedule）
        List<Schedule> schedules = scheduleRepository.findByCourse_CourseId(courseId);
        for (Schedule schedule : schedules) {
            // 删除对应的学员课堂记录
            studentScheduleRecordRepository.deleteBySchedule_ScheduleId(schedule.getScheduleId());
        }

        // 删除排课
        scheduleRepository.deleteAll(schedules);

        // 删除班级学员记录（如果是班级课）
        classStudentRepository.deleteByCourse_CourseId(courseId);

        // 最后删除课程
        courseRepository.delete(course);
    }


    //查询排课详情
    @Override
    public CourseScheduleDetailDTO getScheduleDetailById(Integer scheduleId) {
        Schedule s = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("排课不存在"));

        CourseScheduleDetailDTO dto = new CourseScheduleDetailDTO();
        dto.setScheduleId(s.getScheduleId());
        dto.setClassTime(s.getClassTime());
        dto.setCourseName(s.getCourse().getCourseName());
        dto.setCourseType(s.getCourse().getCourseType());
        dto.setRoomId(s.getRoom().getRoomId());
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
                .findBySchedule_ScheduleIdAndAttendStatus(scheduleId, "请假待批");

        List<LeaveRequestDTO> result = new ArrayList<>();
        for (StudentScheduleRecord r : records) {
            LeaveRequestDTO dto = new LeaveRequestDTO();
            dto.setSsrId(r.getSsrId());
            dto.setStudentId(r.getStudent().getUserId());
            dto.setStudentName(r.getStudent().getUsername());

            Course course = r.getCourse();
            Schedule schedule = r.getSchedule();

            dto.setCourseId(course != null ? course.getCourseId() : null);
            dto.setCourseName(course != null ? course.getCourseName() : "未知课程");

            dto.setScheduleId(schedule != null ? schedule.getScheduleId() : null);
            dto.setClassTime(schedule != null ? schedule.getClassTime() : null);

            dto.setLeaveReason(r.getLeaveReason());
            dto.setAttendStatus(r.getAttendStatus());
            result.add(dto);
        }
        return result;
    }

    //查看所有课程列表
    @Override
    public List<CourseListDTO> getAllCourses() {
        List<Course> courseList = courseRepository.findAll();

        return courseList.stream().map(course -> {
            CourseListDTO dto = new CourseListDTO();
            dto.setCourseId(course.getCourseId());
            dto.setCourseName(course.getCourseName());
            dto.setCourseType(course.getCourseType());
            dto.setCourseContent(course.getCourseContent());
            dto.setTotalHours(course.getTotalHours());
            dto.setRemainingHours(course.getRemainingHours());
            dto.setClassGroupCode(course.getClassGroupCode());
            dto.setStudentId(course.getStudent() != null ? course.getStudent().getUserId() : null);

            if ("1对1".equals(course.getCourseType()) && course.getStudent() != null) {
                dto.setStudentName(course.getStudent().getUsername());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    //查询学员排课记录（简单）
    @Override
    public List<StudentScheduleRecordSimpleDTO> getStudentScheduleRecords(Integer studentId) {
        List<StudentScheduleRecord> records = studentScheduleRecordRepository.findByStudent_UserId(studentId);

        return records.stream().map(record -> {
            StudentScheduleRecordSimpleDTO dto = new StudentScheduleRecordSimpleDTO();
            dto.setSsrId(record.getSsrId());
            if (record.getCourse() != null) {
                dto.setCourseId(record.getCourse().getCourseId());
                dto.setCourseName(record.getCourse().getCourseName());
            }
            if (record.getSchedule() != null) {
                dto.setScheduleId(record.getSchedule().getScheduleId());
                dto.setClassTime(record.getSchedule().getClassTime().toString());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    //查询学员排课记录（完整）
    @Override
    public List<StudentScheduleRecordFullDTO> getStudentScheduleRecordsWithScheduleInfo(Integer studentId) {
        List<StudentScheduleRecord> records = studentScheduleRecordRepository.findByStudent_UserId(studentId);

        return records.stream().map(record -> {
            StudentScheduleRecordFullDTO dto = new StudentScheduleRecordFullDTO();
            dto.setSsrId(record.getSsrId());

            if (record.getCourse() != null) {
                dto.setCourseId(record.getCourse().getCourseId());
                dto.setCourseName(record.getCourse().getCourseName());
            }

            if (record.getSchedule() != null) {
                dto.setScheduleId(record.getSchedule().getScheduleId());
                if (record.getSchedule().getClassTime() != null) {
                    dto.setClassTime(record.getSchedule().getClassTime().toString());
                }
                if (record.getSchedule().getRoom() != null) {
                    dto.setRoomName(record.getSchedule().getRoom().getRoomName());
                }
                if (record.getSchedule().getTeacher() != null) {
                    dto.setTeacherId(record.getSchedule().getTeacher().getUserId());
                    dto.setTeacherName(record.getSchedule().getTeacher().getUsername());
                }
                if (record.getSchedule().getAssistant() != null) {
                    dto.setAssistantId(record.getSchedule().getAssistant().getUserId());
                    dto.setAssistantName(record.getSchedule().getAssistant().getUsername());
                }
            }

            dto.setAttendStatus(record.getAttendStatus());
            dto.setPerformanceEval(record.getPerformanceEval());

            return dto;
        }).collect(Collectors.toList());
    }

    //查询待评价学员记录
    @Override
    public List<StudentScheduleRecordDTO> getUnEvaluatedRecordsByTeacherId(Integer teacherId) {
        List<StudentScheduleRecord> records = studentScheduleRecordRepository.findUnEvaluatedRecordsByTeacherId(teacherId);

        return records.stream().map(r -> {
            StudentScheduleRecordDTO dto = new StudentScheduleRecordDTO();
            dto.setSsrId(r.getSsrId());
            dto.setStudentId(r.getStudent().getUserId());
            dto.setStudentName(r.getStudent().getUsername());
            dto.setScheduleId(r.getSchedule().getScheduleId());
            dto.setClassTime(r.getSchedule().getClassTime());
            dto.setCourseName(r.getCourse().getCourseName());

            // 新增返回字段
            dto.setAttendStatus(r.getAttendStatus());
            dto.setLeaveReason(r.getLeaveReason());

            return dto;
        }).collect(Collectors.toList());
    }
}
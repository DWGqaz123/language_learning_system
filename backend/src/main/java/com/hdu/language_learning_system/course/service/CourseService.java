package com.hdu.language_learning_system.course.service;

import com.hdu.language_learning_system.course.dto.*;

import java.util.List;

public interface CourseService {
    void createCourse(CourseCreateDTO dto);

    void generateStudentRecords(Integer scheduleId);

    void submitLeaveRequest(LeaveRequestDTO dto);

    CourseAttendanceRecordDTO getAttendanceStatsByCourseId(Integer courseId);

    void updateAttendanceStatus(AttendanceStatusDTO dto);

    void updatePerformanceEval(PerformanceEvalDTO dto);

    List<CourseScheduleDTO> getScheduleByCourseId(Integer courseId);

    List<CourseScheduleDTO> getSchedulesByUserId(Integer userId);

    List<ScheduleStudentStatusDTO> getStudentStatusByScheduleId(Integer scheduleId);

    List<StudentAttendancePerformanceDTO> getStudentAttendancePerformance(Integer studentId);

    List<UserCourseDTO> getCoursesByUserId(Integer userId);

    void reviewLeaveRequest(LeaveReviewDTO dto);

    void updateCourse(CourseUpdateDTO dto);

    void deleteCourse(Integer courseId);

    CourseScheduleDetailDTO getScheduleDetailById(Integer scheduleId);

    List<LeaveRequestDTO> getLeaveRequestsByCourseAndSchedule(Integer courseId, Integer scheduleId);

    List<CourseListDTO> getAllCourses();


    void addClassStudent(ClassStudentOperationDTO dto);

    void removeClassStudent(ClassStudentOperationDTO dto);

    List<ClassStudentInfoDTO> getClassStudentList(Integer courseId);
}
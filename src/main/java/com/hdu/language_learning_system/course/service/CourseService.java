package com.hdu.language_learning_system.course.service;

import com.hdu.language_learning_system.course.dto.*;

import java.util.List;

public interface CourseService {
    void createCourse(CourseCreateDTO dto);

    void addStudentsToClassCourse(AddStudentsToCourseDTO dto);

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

    void removeStudentFromCourse(Integer courseId, Integer studentId);

    CourseScheduleDetailDTO getScheduleDetailById(Integer scheduleId);

    List<LeaveRequestDTO> getLeaveRequestsByCourseAndSchedule(Integer courseId, Integer scheduleId);
}
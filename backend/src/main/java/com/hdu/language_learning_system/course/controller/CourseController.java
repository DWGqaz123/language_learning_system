package com.hdu.language_learning_system.course.controller;

import com.hdu.language_learning_system.common.ApiResponse;
import com.hdu.language_learning_system.course.dto.*;
import com.hdu.language_learning_system.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // 创建课程
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createCourse(@RequestBody CourseCreateDTO dto) {
        courseService.createCourse(dto);
        return ResponseEntity.ok(ApiResponse.success("课程创建成功", null));
    }

    //添加学员到班级
    @PostMapping("/add-class-student")
    public ApiResponse<Void> addClassStudent(@RequestBody ClassStudentOperationDTO dto) {
        courseService.addClassStudent(dto);
        return ApiResponse.success("添加班级学员成功", null);
    }

    //删除班级里的学员
    @DeleteMapping("/remove-class-student")
    public ApiResponse<Void> removeClassStudent(@RequestBody ClassStudentOperationDTO dto) {
        courseService.removeClassStudent(dto);
        return ApiResponse.success("移除班级学员成功", null);
    }

    //查询课程的班级学员列表
    @GetMapping("/get-class-students")
    public ApiResponse<List<ClassStudentInfoDTO>> getClassStudents(@RequestParam Integer courseId) {
        return ApiResponse.success(courseService.getClassStudentList(courseId));
    }

    //创建学员上课记录
    @PostMapping("/student-records/generate")
    public ResponseEntity<ApiResponse<Void>> generateStudentScheduleRecords(@RequestBody GenerateStudentRecordsDTO dto) {
        try {
            courseService.generateStudentRecords(dto.getScheduleId());
            return ResponseEntity.ok(ApiResponse.success("学生课程记录生成成功", null));
        } catch (RuntimeException e) {
            // 返回错误响应，保持与其他接口一致的格式
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    //学员请假
    @PostMapping("/student/leave-request")
    public ResponseEntity<String> submitLeaveRequest(@RequestBody LeaveRequestDTO dto) {
        try {
            courseService.submitLeaveRequest(dto);
            return ResponseEntity.ok("请假申请提交成功");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("提交失败：" + e.getMessage());
        }
    }

    //课程考勤记录查询
    @GetMapping("/attendance-record")
    public ResponseEntity<CourseAttendanceRecordDTO> getAttendanceStats(@RequestParam Integer courseId) {
        CourseAttendanceRecordDTO dto = courseService.getAttendanceStatsByCourseId(courseId);
        return ResponseEntity.ok(dto);
    }

    //课堂考勤
    @PostMapping("/update-attendance-status")
    public ResponseEntity<String> updateAttendanceStatus(@RequestBody AttendanceStatusDTO dto) {
        courseService.updateAttendanceStatus(dto);
        return ResponseEntity.ok("考勤状态更新成功");
    }

    //教师课堂表现评价
    @PostMapping("/update-performance-eval")
    public ResponseEntity<String> updatePerformanceEval(@RequestBody PerformanceEvalDTO dto) {
        courseService.updatePerformanceEval(dto);
        return ResponseEntity.ok("课堂表现评价更新成功");
    }


    // 查询课程课表
    @GetMapping("/{courseId}/schedules")
    public ApiResponse<List<CourseScheduleDTO>> getCourseSchedules(@PathVariable Integer courseId) {
        List<CourseScheduleDTO> schedules = courseService.getScheduleByCourseId(courseId);
        return ApiResponse.success(schedules);
    }

    // 分角色查看课表
    @GetMapping("/my-schedules")
    public ApiResponse<List<CourseScheduleDTO>> getMySchedules(@RequestParam Integer userId) {
        List<CourseScheduleDTO> list = courseService.getSchedulesByUserId(userId);
        return ApiResponse.success(list);
    }

    //查看某次课考勤
    @GetMapping("/schedule/{scheduleId}/students-status")
    public ApiResponse<List<ScheduleStudentStatusDTO>> getStudentStatusBySchedule(
            @PathVariable Integer scheduleId) {
        List<ScheduleStudentStatusDTO> result = courseService.getStudentStatusByScheduleId(scheduleId);
        return ApiResponse.success(result);
    }

    //学员查看自己课堂表现
    @GetMapping("/student/attendance-performance")
    public ApiResponse<List<StudentAttendancePerformanceDTO>> getStudentAttendancePerformance(
            @RequestParam Integer studentId) {
        List<StudentAttendancePerformanceDTO> result = courseService.getStudentAttendancePerformance(studentId);
        return ApiResponse.success(result);
    }

    //根据用户查询课程
    @GetMapping("/my-courses")
    public ApiResponse<List<UserCourseDTO>> getMyCourses(@RequestParam Integer userId) {
        List<UserCourseDTO> courses = courseService.getCoursesByUserId(userId);
        return ApiResponse.success(courses);
    }

    //审核学员请假
    @PostMapping("/review-leave")
    public ApiResponse<String> reviewLeave(@RequestBody LeaveReviewDTO dto) {
        courseService.reviewLeaveRequest(dto);
        return ApiResponse.success("审核处理完成");
    }

    //修改课程信息
    @PutMapping("/update")
    public ApiResponse<String> updateCourse(@RequestBody CourseUpdateDTO dto) {
        courseService.updateCourse(dto);
        return ApiResponse.success("课程信息更新成功");
    }

    //删除课程
    @DeleteMapping("/{courseId}")
    public ApiResponse<String> deleteCourse(@PathVariable Integer courseId) {
        courseService.deleteCourse(courseId);
        return ApiResponse.success("课程删除成功");
    }


    //查询排课详情
    @GetMapping("/schedules/{scheduleId}")
    public ApiResponse<CourseScheduleDetailDTO> getScheduleDetail(@PathVariable Integer scheduleId) {
        CourseScheduleDetailDTO detail = courseService.getScheduleDetailById(scheduleId);
        return ApiResponse.success(detail);
    }

    //查询某课程某课表下的请假申请
    @GetMapping("/{courseId}/schedule/{scheduleId}/leave-requests")
    public ApiResponse<List<LeaveRequestDTO>> getLeaveRequestsByCourseAndSchedule(
            @PathVariable Integer courseId,
            @PathVariable Integer scheduleId
    ) {
        List<LeaveRequestDTO> list = courseService.getLeaveRequestsByCourseAndSchedule(courseId, scheduleId);
        return ApiResponse.success(list);
    }
    //查看所有课程列表
    @GetMapping("/all")
    public ApiResponse<List<CourseListDTO>> getAllCourses() {
        List<CourseListDTO> list = courseService.getAllCourses();
        return ApiResponse.success(list);
    }
}

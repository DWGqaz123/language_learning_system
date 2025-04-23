package com.hdu.language_learning_system.analysis.controller;

import com.hdu.language_learning_system.analysis.dto.*;
import com.hdu.language_learning_system.analysis.service.AnalysisService;
import com.hdu.language_learning_system.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    //课程进度统计
    @GetMapping("/course-progress")
    public ApiResponse<CourseProgressDTO> getCourseProgress(@RequestParam Integer studentId) {
        try {
            CourseProgressDTO dto = analysisService.getCourseProgress(studentId);
            return ApiResponse.success(dto);
        } catch (Exception e) {
            return ApiResponse.error("获取课程进度失败：" + e.getMessage());
        }
    }

    //课程考勤统计
    @GetMapping("/attendance-stats")
    public ApiResponse<AttendanceStatsDTO> getAttendanceStats(@RequestParam Integer studentId) {
        AttendanceStatsDTO dto = analysisService.getAttendanceStats(studentId);
        return ApiResponse.success(dto);
    }

    //任务数据统计（完成率、平均分）
    @GetMapping("/task-statistics/{studentId}")
    public ApiResponse<TaskStatisticsDTO> getTaskStatistics(@PathVariable Integer studentId) {
        try {
            TaskStatisticsDTO dto = analysisService.getTaskStatistics(studentId);
            return ApiResponse.success(dto);
        } catch (Exception e) {
            return ApiResponse.error("获取任务统计失败：" + e.getMessage());
        }
    }

    //模拟考试统计
    @GetMapping("/mock-exams/statistics/{studentId}")
    public ApiResponse<StudentMockExamStatSimpleDTO> getMockExamStats(@PathVariable Integer studentId) {
        try {
            StudentMockExamStatSimpleDTO stat = analysisService.getMockExamStatByStudentId(studentId);
            return ApiResponse.success(stat);
        } catch (Exception e) {
            return ApiResponse.error("获取模拟考试统计失败：" + e.getMessage());
        }
    }
}
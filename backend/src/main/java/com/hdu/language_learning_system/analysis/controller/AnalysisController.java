package com.hdu.language_learning_system.analysis.controller;

import com.hdu.language_learning_system.analysis.dto.*;
import com.hdu.language_learning_system.analysis.repository.StudentPerformanceReportRepository;
import com.hdu.language_learning_system.analysis.service.AnalysisService;
import com.hdu.language_learning_system.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/mock-exams/score-trend/{studentId}")
    public ApiResponse<List<ExamScoreTrendDTO>> getExamScoreTrend(@PathVariable Integer studentId) {
        try {
            List<ExamScoreTrendDTO> trend = analysisService.getExamScoreTrend(studentId);
            return ApiResponse.success(trend);
        } catch (Exception e) {
            return ApiResponse.error("获取考试趋势失败：" + e.getMessage());
        }
    }

    // 更新助教点评
    @PutMapping("/update-comment")
    public ResponseEntity<ApiResponse<Void>> updateAssistantComment(@RequestBody PerformanceCommentDTO dto) {
        try {
            analysisService.updateAssistantComment(dto);
            return ResponseEntity.ok(ApiResponse.success("助教点评更新成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新失败：" + e.getMessage()));
        }
    }

    // 获取单个报告详情
    @GetMapping("/report")
    public ResponseEntity<ApiResponse<StudentPerformanceReportDTO>> getReportById(@RequestParam Integer reportId) {
        try {
            StudentPerformanceReportDTO dto = analysisService.getReportById(reportId);
            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取失败：" + e.getMessage()));
        }
    }

    // 获取所有报告（列表）
    @GetMapping("/all-reports")
    public ResponseEntity<ApiResponse<List<StudentPerformanceReportDTO>>> getAllReports() {
        List<StudentPerformanceReportDTO> list = analysisService.getAllReports();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    //生成学员报告
    @PostMapping("/generate-report")
    public ResponseEntity<ApiResponse<Void>> generateReport(@RequestBody GenerateReportRequestDTO dto) {
        try {
            analysisService.generatePerformanceReport(dto);
            return ResponseEntity.ok(ApiResponse.success("学习表现报告生成成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("生成失败：" + e.getMessage()));
        }
    }

    // 查询某个学员的所有报告
    @GetMapping("/student-reports")
    public ApiResponse<List<StudentPerformanceReportDTO>> getReportsByStudentId(@RequestParam Integer studentId) {
        List<StudentPerformanceReportDTO> list = analysisService.getReportsByStudentId(studentId);
        return ApiResponse.success(list);
    }
    //获取最新的学员报告
    @GetMapping("/latest-report")
    public ApiResponse<StudentPerformanceReportDTO> getLatestReport(@RequestParam Integer studentId) {
        return analysisService.getLatestReportByStudentId(studentId);
    }


    // 获取学员的周分析报告
    @GetMapping("/weekly-report")
    public ApiResponse<WeeklyAnalysisDTO> getWeeklyAnalysis(@RequestParam Integer studentId) {
        WeeklyAnalysisDTO dto = analysisService.getWeeklyAnalysis(studentId);
        return ApiResponse.success(dto);
    }

    // 获取学员的阶段性分析报告
    @GetMapping("/stage-report")
    public ApiResponse<StageAnalysisDTO> getStageAnalysis(@RequestParam Integer studentId) {
        StageAnalysisDTO dto = analysisService.getStageAnalysis(studentId);
        return ApiResponse.success(dto);
    }

}
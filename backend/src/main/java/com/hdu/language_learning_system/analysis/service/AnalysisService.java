package com.hdu.language_learning_system.analysis.service;

import com.hdu.language_learning_system.analysis.dto.*;

import java.util.List;

public interface AnalysisService {
    CourseProgressDTO getCourseProgress(Integer studentId);

    AttendanceStatsDTO getAttendanceStats(Integer studentId);

    TaskStatisticsDTO getTaskStatistics(Integer studentId);

    StudentMockExamStatSimpleDTO getMockExamStatByStudentId(Integer studentId);

    StudentPerformanceReportDTO getReportById(Integer reportId);

    void updateAssistantComment(PerformanceCommentDTO dto);
    List<StudentPerformanceReportDTO> getAllReports();

    void generatePerformanceReport(GenerateReportRequestDTO request);

    // 查询某个学员的所有报告
    List<StudentPerformanceReportDTO> getReportsByStudentId(Integer studentId);
}

package com.hdu.language_learning_system.analysis.service;

import com.hdu.language_learning_system.analysis.dto.*;

public interface AnalysisService {
    CourseProgressDTO getCourseProgress(Integer studentId);

    AttendanceStatsDTO getAttendanceStats(Integer studentId);

    TaskStatisticsDTO getTaskStatistics(Integer studentId);

    StudentMockExamStatSimpleDTO getMockExamStatByStudentId(Integer studentId);
}

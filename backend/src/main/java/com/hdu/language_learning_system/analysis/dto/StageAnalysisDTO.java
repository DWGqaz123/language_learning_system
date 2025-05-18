package com.hdu.language_learning_system.analysis.dto;

import com.hdu.language_learning_system.studyRoom.dto.StudyRoomUsageStatisticsDTO;
import lombok.Data;

import java.util.List;

@Data
public class StageAnalysisDTO {
    private Integer studentId;
    private CourseProgressDTO courseProgress;
    private AttendanceStatsDTO attendanceStats;
    private TaskStatisticsDTO taskStats;
    private StudentMockExamStatSimpleDTO examStats;
    private List<ExamScoreTrendDTO> examScoreTrend; // 模拟考试趋势（用于阶段性分析）
    private StudyRoomUsageStatisticsDTO studyRoomStats;
}
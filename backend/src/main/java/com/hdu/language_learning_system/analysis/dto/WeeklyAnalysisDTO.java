package com.hdu.language_learning_system.analysis.dto;

import com.hdu.language_learning_system.studyRoom.dto.StudyRoomUsageStatisticsDTO;
import lombok.Data;

@Data
public class WeeklyAnalysisDTO {
    private Integer studentId;
    private CourseProgressDTO courseProgress;
    private AttendanceStatsDTO attendanceStats;
    private TaskStatisticsDTO taskStats;
    private StudentMockExamStatSimpleDTO examStats;
    private StudyRoomUsageStatisticsDTO studyRoomStats;
}
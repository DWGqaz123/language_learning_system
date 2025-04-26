package com.hdu.language_learning_system.course.dto;

import lombok.Data;

@Data
public class StudentScheduleRecordFullDTO {
    private Integer ssrId;

    // 课程信息
    private Integer courseId;
    private String courseName;

    // 排课信息
    private Integer scheduleId;
    private String classTime;
    private String roomName;
    private Integer teacherId;
    private String teacherName;
    private Integer assistantId;
    private String assistantName;

    // 出勤状态
    private String attendStatus;
    private String performanceEval;
}
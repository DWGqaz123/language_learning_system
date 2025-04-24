package com.hdu.language_learning_system.course.dto;

import lombok.Data;

@Data
public class AttendanceStatusDTO {
    private Integer scheduleId;    // 课表ID
    private Integer studentId;     // 学员ID
    private String attendStatus;   // 出勤状态：出勤 / 缺勤 / 请假 / 请假待批 / 未开始
}
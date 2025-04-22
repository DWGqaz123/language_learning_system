package com.hdu.language_learning_system.studyRoom.dto;

import lombok.Data;

@Data
public class StudyRoomUsageStatsDTO {
    private String timeSlot;        // 上午 / 下午 / 晚上
    private int totalReservations;  // 预约人数
    private int signedInCount;      // 已签到人数
    private int signedOutCount;     // 已签退人数
}
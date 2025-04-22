package com.hdu.language_learning_system.studyRoom.dto;

import lombok.Data;

import java.util.Map;

@Data
public class StudyRoomDTO {

    private Integer roomId;

    private String roomName;

    private Integer capacity;

    private String location;

    // 每个时间段的预约状态（key: 上午/下午/晚上，value: 可预约/不可预约/已预约）
    private Map<String, String> timeSlotStatus;
}
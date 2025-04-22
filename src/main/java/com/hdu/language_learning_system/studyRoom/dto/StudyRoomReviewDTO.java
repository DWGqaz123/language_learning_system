package com.hdu.language_learning_system.studyRoom.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StudyRoomReviewDTO {
    private Integer roomId;
    private Integer studentId;
    private LocalDate reservationDate;
    private String timeSlot;     // 上午 / 下午 / 晚上
    private String reviewStatus; // 通过 / 驳回
}
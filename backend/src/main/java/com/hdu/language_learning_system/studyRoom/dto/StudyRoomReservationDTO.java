package com.hdu.language_learning_system.studyRoom.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StudyRoomReservationDTO {
    private Integer roomId;
    private Integer studentId;
    private LocalDate reservationDate;
    private String timeSlot;
    private String reviewStatus;
    private Integer capacity;

    // 附加信息（展示用）
    private String roomName;
    private String studentName;
    private String location;
}
package com.hdu.language_learning_system.studyRoom.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

// 用于复合主键的类
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyRoomReservationId implements Serializable {
    private Integer roomId;
    private Integer studentId;
    private LocalDate reservationDate;
    private String timeSlot;
}
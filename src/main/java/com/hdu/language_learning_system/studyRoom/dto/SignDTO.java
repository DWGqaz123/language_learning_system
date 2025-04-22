package com.hdu.language_learning_system.studyRoom.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SignDTO {
    private Integer roomId;
    private Integer studentId;
    private LocalDate reservationDate;
    private String timeSlot; // 上午/下午/晚上
    private String action; // "signIn" or "signOut"
}
package com.hdu.language_learning_system.studyRoom.dto;

import lombok.Data;

@Data
public class StudyRoomListDTO {
    private Integer roomId;
    private String roomName;
    private Integer capacity;
    private String location;
    private String currentStatus;
}
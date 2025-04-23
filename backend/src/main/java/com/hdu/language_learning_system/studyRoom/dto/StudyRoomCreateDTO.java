package com.hdu.language_learning_system.studyRoom.dto;

import lombok.Data;

@Data
public class StudyRoomCreateDTO {
    private String roomName;
    private Integer capacity;
    private String location;
}
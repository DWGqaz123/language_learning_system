package com.hdu.language_learning_system.exam.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MockExamCreateDTO {
    private String examName;
    private Timestamp examTime;
    private Integer standardPaperId;
    private Integer examRoomId;
}
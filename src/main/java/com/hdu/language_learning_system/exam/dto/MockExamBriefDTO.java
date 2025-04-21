package com.hdu.language_learning_system.exam.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MockExamBriefDTO {
    private Integer examId;
    private String examName;
    private Timestamp examTime;
    private String examRoomName;
    private String paperName;
}
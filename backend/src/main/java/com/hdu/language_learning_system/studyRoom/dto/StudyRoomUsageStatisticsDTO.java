package com.hdu.language_learning_system.studyRoom.dto;

import lombok.Data;

@Data
public class StudyRoomUsageStatisticsDTO {
    private int totalUsageCount;
    private int morningCount;
    private int afternoonCount;
    private int eveningCount;
}
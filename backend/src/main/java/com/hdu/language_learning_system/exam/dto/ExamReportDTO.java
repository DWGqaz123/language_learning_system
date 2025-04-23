package com.hdu.language_learning_system.exam.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class ExamReportDTO {
    private Integer examId;
    private String examName;
    private Timestamp examTime;
    private String paperName;
    private String examRoomName;

    private Integer totalScore;
    private Integer objectiveScore;
    private Integer subjectiveScore;

    private String teacherComment;
    private String assistantComment;
    private Timestamp completedTime;

    private List<StudentAnswerDTO> answers;
}
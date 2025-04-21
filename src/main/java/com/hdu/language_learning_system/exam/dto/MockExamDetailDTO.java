package com.hdu.language_learning_system.exam.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class MockExamDetailDTO {
    private Integer examId;
    private String examName;
    private Timestamp examTime;
    private String paperName;
    private String examRoomName;
    private String location;

    // 以下字段在学员传入 studentId 时才有值
    private Integer totalScore;
    private Integer subjectiveScore;
    private String teacherComment;
    private String assistantComment;
    private Timestamp completedTime;
    private List<QuestionDTO> paperContent;
    private List<StudentAnswerDTO> objectiveAnswersJson;
    private List<StudentAnswerDTO> answers; // 学员提交的所有答案（含主观题）

}
package com.hdu.language_learning_system.exam.service;

import com.hdu.language_learning_system.exam.dto.*;

import java.util.List;

public interface MockExamService {
    void createMockExam(MockExamCreateDTO dto);

    void addStudentsToExam(ExamStudentAddDTO dto);

    List<MockExamBriefDTO> getAllMockExams();

    List<StudentExamRecordDTO> getStudentExamRecords(Integer studentId);

    MockExamDetailDTO getExamDetail(Integer examId, Integer studentId);

    ExamPaperContentDTO getExamPaperContent(Integer examId, Integer studentId);
    void submitExamAnswers(ExamAnswerSubmitDTO dto);

    void autoGradeObjectiveQuestions(Integer examId, Integer studentId);

    void deleteMockExamById(Integer examId);

    void gradeSubjectiveQuestion(SubjectiveScoreDTO dto);

    StudentExamDetailDTO getStudentExamDetail(Integer examId, Integer studentId);

    void submitAssistantComment(AssistantCommentDTO dto);

    ExamReportDTO getExamReport(Integer examId, Integer studentId);
}
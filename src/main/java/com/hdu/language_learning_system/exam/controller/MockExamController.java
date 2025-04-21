package com.hdu.language_learning_system.exam.controller;

import com.hdu.language_learning_system.common.ApiResponse;
import com.hdu.language_learning_system.course.repository.StudentScheduleRecordRepository;
import com.hdu.language_learning_system.exam.dto.*;
import com.hdu.language_learning_system.exam.entity.StudentExamRecord;
import com.hdu.language_learning_system.exam.repository.StudentExamRecordRepository;
import com.hdu.language_learning_system.exam.service.*;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mock-exams")
public class MockExamController {

    @Resource
    private MockExamService mockExamService;

    @Resource
    private MockExamService examService;

    @Resource
    StudentExamRecordRepository studentExamRecordRepository;

    //创建模拟考试
    @PostMapping("/create")
    public ApiResponse<String> createMockExam(@RequestBody MockExamCreateDTO dto) {
        mockExamService.createMockExam(dto);
        return ApiResponse.success("模拟考试创建成功");
    }


    // 添加学员并发送考试通知
    @PostMapping("/add-students")
    public ApiResponse<String> addStudentsToExam(@RequestBody ExamStudentAddDTO dto) {
        examService.addStudentsToExam(dto);
        return ApiResponse.success("已添加学员并发送考试通知");
    }

    //助教查看所有考试列表
    @GetMapping("/all")
    public List<MockExamBriefDTO> getAllMockExams() {
        return mockExamService.getAllMockExams();
    }

    //学员查看自己的考试列表
    @GetMapping("/my-list")
    public ApiResponse<List<StudentExamRecordDTO>> getMyExamList(@RequestParam Integer studentId) {
        List<StudentExamRecordDTO> list = mockExamService.getStudentExamRecords(studentId);
        return ApiResponse.success(list);
    }

    //查看考试详情
    @GetMapping("/detail")
    public ApiResponse<MockExamDetailDTO> getExamDetail(
            @RequestParam Integer examId,
            @RequestParam(required = false) Integer studentId) {
        MockExamDetailDTO detail = mockExamService.getExamDetail(examId, studentId);
        return ApiResponse.success(detail);
    }

    //学员查看试卷
    @GetMapping("/paper")
    public ApiResponse<ExamPaperContentDTO> getExamPaperContent(
            @RequestParam Integer examId,
            @RequestParam Integer studentId) {
        try {
            ExamPaperContentDTO dto = mockExamService.getExamPaperContent(examId, studentId);
            return ApiResponse.success(dto);
        } catch (Exception e) {
            return ApiResponse.error("获取试卷失败：" + e.getMessage());
        }
    }

    //学员提交考试答题信息
    @PostMapping("/submit-answers")
    public ApiResponse<String> submitExamAnswers(@RequestBody ExamAnswerSubmitDTO dto) {
        try {
            mockExamService.submitExamAnswers(dto);
            return ApiResponse.success("提交成功", null);
        } catch (Exception e) {
            return ApiResponse.error("提交失败：" + e.getMessage());
        }
    }

    //系统自动批改客观题
    @PostMapping("/auto-grade-objective")
    public ApiResponse<String> autoGradeObjectiveQuestions(@RequestParam Integer examId,
                                                           @RequestParam Integer studentId) {
        try {
            mockExamService.autoGradeObjectiveQuestions(examId, studentId);
            return ApiResponse.success("客观题自动批改完成", null);
        } catch (Exception e) {
            return ApiResponse.error("批改失败：" + e.getMessage());
        }
    }
    // 删除模拟考试
    @DeleteMapping("/{examId}")
    public ApiResponse<String> deleteMockExam(@PathVariable Integer examId) {
        try {
            mockExamService.deleteMockExamById(examId);
            return ApiResponse.success("删除成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error("删除失败：" + e.getMessage());
        }
    }
}
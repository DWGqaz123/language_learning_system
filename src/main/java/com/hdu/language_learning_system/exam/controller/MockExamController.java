package com.hdu.language_learning_system.exam.controller;

import com.hdu.language_learning_system.common.ApiResponse;
import com.hdu.language_learning_system.exam.dto.MockExamCreateDTO;
import com.hdu.language_learning_system.exam.service.MockExamService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mock-exams")
public class MockExamController {

    @Resource
    private MockExamService mockExamService;

    @PostMapping("/create")
    public ApiResponse<String> createMockExam(@RequestBody MockExamCreateDTO dto) {
        mockExamService.createMockExam(dto);
        return ApiResponse.success("模拟考试创建成功");
    }
}
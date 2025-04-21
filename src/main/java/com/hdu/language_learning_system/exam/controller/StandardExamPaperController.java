package com.hdu.language_learning_system.exam.controller;

import com.hdu.language_learning_system.common.ApiResponse;
import com.hdu.language_learning_system.exam.dto.*;
import com.hdu.language_learning_system.exam.service.StandardExamPaperService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exams/papers")
public class StandardExamPaperController {

    @Resource
    private StandardExamPaperService standardExamPaperService;

    @PostMapping("/create")
    public ApiResponse<String> createPaper(@RequestBody StandardExamPaperDTO dto) {
        standardExamPaperService.createStandardExamPaper(dto);
        return ApiResponse.success("试卷创建成功");
    }
}
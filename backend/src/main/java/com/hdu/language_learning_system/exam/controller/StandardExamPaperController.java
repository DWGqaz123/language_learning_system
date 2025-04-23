package com.hdu.language_learning_system.exam.controller;

import com.hdu.language_learning_system.common.ApiResponse;
import com.hdu.language_learning_system.exam.dto.*;
import com.hdu.language_learning_system.exam.service.StandardExamPaperService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    //获取所有试卷列表
    @GetMapping("/standard-papers")
    public ApiResponse<List<StandardExamPaperDTO>> getAllStandardPapers() {
        try {
            List<StandardExamPaperDTO> list = standardExamPaperService.getAllStandardExamPapers();
            return ApiResponse.success(list);
        } catch (Exception e) {
            return ApiResponse.error("获取试卷列表失败：" + e.getMessage());
        }
    }
}
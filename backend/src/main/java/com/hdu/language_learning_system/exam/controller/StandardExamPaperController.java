package com.hdu.language_learning_system.exam.controller;

import com.hdu.language_learning_system.common.ApiResponse;
import com.hdu.language_learning_system.exam.dto.*;
import com.hdu.language_learning_system.exam.service.StandardExamPaperService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams/papers")
public class StandardExamPaperController {

    @Resource
    private StandardExamPaperService standardExamPaperService;


    //创建试卷
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
    // 编辑试卷
    @PutMapping("/editPaper")
    public ResponseEntity<ApiResponse<Void>> updateExamPaper(@RequestBody StandardExamPaperDTO dto) {
        standardExamPaperService.updateStandardExamPaper(dto);
        return ResponseEntity.ok(ApiResponse.success("试卷更新成功", null));
    }

    // 删除试卷
    @DeleteMapping("/{paperId}")
    public ResponseEntity<ApiResponse<Void>> deleteExamPaper(@PathVariable Integer paperId) {
        standardExamPaperService.deleteStandardExamPaper(paperId);
        return ResponseEntity.ok(ApiResponse.success("试卷删除成功", null));
    }

    // 查看试卷详情
    @GetMapping("/{paperId}")
    public ResponseEntity<ApiResponse<StandardExamPaperDTO>> getExamPaper(@PathVariable Integer paperId) {
        return ResponseEntity.ok(ApiResponse.success(standardExamPaperService.getStandardExamPaperById(paperId)));
    }

    //查询待审核试卷列表
    @GetMapping("/pending-papers")
    public ApiResponse<List<StandardExamPaperDTO>> getPendingPapers() {
        List<StandardExamPaperDTO> papers = standardExamPaperService.getPendingPapers();
        return ApiResponse.success(papers);
    }

    //审核试卷
    @PostMapping("/audit")
    public ApiResponse<String> auditPaper(@RequestBody ExamPaperAuditDTO dto) {
        standardExamPaperService.auditPaper(dto);
        return ApiResponse.success("审核完成");
    }

}
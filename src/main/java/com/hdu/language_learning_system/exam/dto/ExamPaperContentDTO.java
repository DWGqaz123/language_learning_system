package com.hdu.language_learning_system.exam.dto;

import lombok.Data;

@Data
public class ExamPaperContentDTO {
    private String paperName;
    private String examType;
    private String paperContentJson; // 原始试卷内容（JSON 字符串）
}
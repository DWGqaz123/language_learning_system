package com.hdu.language_learning_system.exam.dto;

import lombok.Data;

@Data
public class ExamPaperAuditDTO {
    private Integer paperId;
    private Integer teacherId;
    private String auditStatus; // 通过、驳回
    private String auditComment;
}

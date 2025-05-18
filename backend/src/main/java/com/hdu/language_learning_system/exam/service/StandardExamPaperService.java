package com.hdu.language_learning_system.exam.service;

import com.hdu.language_learning_system.exam.dto.ExamPaperAuditDTO;
import com.hdu.language_learning_system.exam.dto.StandardExamPaperDTO;

import java.util.List;

// Service 接口
public interface StandardExamPaperService {
    void createStandardExamPaper(StandardExamPaperDTO dto);

    List<StandardExamPaperDTO> getAllStandardExamPapers();

    void updateStandardExamPaper(StandardExamPaperDTO dto);
    void deleteStandardExamPaper(Integer paperId);
    StandardExamPaperDTO getStandardExamPaperById(Integer paperId);

    List<StandardExamPaperDTO> getPendingPapers();
    void auditPaper(ExamPaperAuditDTO dto);
}
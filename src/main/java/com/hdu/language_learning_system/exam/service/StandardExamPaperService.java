package com.hdu.language_learning_system.exam.service;

import com.hdu.language_learning_system.exam.dto.StandardExamPaperDTO;

import java.util.List;

// Service 接口
public interface StandardExamPaperService {
    void createStandardExamPaper(StandardExamPaperDTO dto);

    List<StandardExamPaperDTO> getAllStandardExamPapers();
}
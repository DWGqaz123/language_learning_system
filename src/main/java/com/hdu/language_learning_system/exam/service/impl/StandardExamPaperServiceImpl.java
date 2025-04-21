package com.hdu.language_learning_system.exam.service.impl;

import com.hdu.language_learning_system.exam.repository.*;
import com.hdu.language_learning_system.exam.dto.*;
import com.hdu.language_learning_system.exam.entity.*;
import com.hdu.language_learning_system.exam.service.StandardExamPaperService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class StandardExamPaperServiceImpl implements StandardExamPaperService {

    @Resource
    private StandardExamPaperRepository standardExamPaperRepository;

    //添加新试卷
    @Override
    public void createStandardExamPaper(StandardExamPaperDTO dto) {
        StandardExamPaper paper = new StandardExamPaper();
        paper.setPaperName(dto.getPaperName());
        paper.setExamType(dto.getExamType());
        paper.setCreatedTime(new Timestamp(System.currentTimeMillis()));

		paper.setPaperContent(dto.getPaperContentJson());
		paper.setObjectiveAnswersJson(dto.getObjectiveAnswersJson());

        standardExamPaperRepository.save(paper);
    }
}
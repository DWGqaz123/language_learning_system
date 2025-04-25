package com.hdu.language_learning_system.exam.service.impl;

import com.hdu.language_learning_system.exam.repository.*;
import com.hdu.language_learning_system.exam.dto.*;
import com.hdu.language_learning_system.exam.entity.*;
import com.hdu.language_learning_system.exam.service.StandardExamPaperService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

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

    //查询所有试卷列表
    @Override
    public List<StandardExamPaperDTO> getAllStandardExamPapers() {
        List<StandardExamPaper> papers = standardExamPaperRepository.findAll();

        return papers.stream().map(paper -> {
            StandardExamPaperDTO dto = new StandardExamPaperDTO();
            dto.setPaperId(paper.getPaperId());
            dto.setPaperName(paper.getPaperName());
            dto.setExamType(paper.getExamType());
            dto.setCreatedTime(paper.getCreatedTime());
            return dto;
        }).collect(Collectors.toList());
    }

    // 编辑试卷
    @Override
    public void updateStandardExamPaper(StandardExamPaperDTO dto) {
        StandardExamPaper paper = standardExamPaperRepository.findById(dto.getPaperId())
                .orElseThrow(() -> new RuntimeException("试卷不存在"));

        if (dto.getPaperName() != null) {
            paper.setPaperName(dto.getPaperName());
        }
        if (dto.getExamType() != null) {
            paper.setExamType(dto.getExamType());
        }
        if (dto.getPaperContentJson() != null) {
            paper.setPaperContent(dto.getPaperContentJson());
        }
        if (dto.getObjectiveAnswersJson() != null) {
            paper.setObjectiveAnswersJson(dto.getObjectiveAnswersJson());
        }

        standardExamPaperRepository.save(paper);
    }

    // 删除试卷
    @Override
    public void deleteStandardExamPaper(Integer paperId) {
        if (!standardExamPaperRepository.existsById(paperId)) {
            throw new RuntimeException("试卷不存在，无法删除");
        }
        standardExamPaperRepository.deleteById(paperId);
    }

    // 查看试卷详情
    @Override
    public StandardExamPaperDTO getStandardExamPaperById(Integer paperId) {
        StandardExamPaper paper = standardExamPaperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("试卷不存在"));

        StandardExamPaperDTO dto = new StandardExamPaperDTO();
        dto.setPaperId(paper.getPaperId());
        dto.setPaperName(paper.getPaperName());
        dto.setExamType(paper.getExamType());
        dto.setCreatedTime(paper.getCreatedTime());
        dto.setPaperContentJson(paper.getPaperContent());
        dto.setObjectiveAnswersJson(paper.getObjectiveAnswersJson());

        return dto;
    }

}
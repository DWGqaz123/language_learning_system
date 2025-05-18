package com.hdu.language_learning_system.exam.service.impl;

import com.hdu.language_learning_system.exam.repository.*;
import com.hdu.language_learning_system.exam.dto.*;
import com.hdu.language_learning_system.exam.entity.*;
import com.hdu.language_learning_system.exam.service.StandardExamPaperService;
import com.hdu.language_learning_system.user.entity.User;
import com.hdu.language_learning_system.user.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StandardExamPaperServiceImpl implements StandardExamPaperService {

    @Resource
    private StandardExamPaperRepository standardExamPaperRepository;

    @Resource
    private UserRepository userRepository;

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
    //审核新试卷
    @Override
    public List<StandardExamPaperDTO> getPendingPapers() {
        List<StandardExamPaper> papers = standardExamPaperRepository.findByAuditStatus("待审核");
        return papers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private StandardExamPaperDTO convertToDTO(StandardExamPaper paper) {
        StandardExamPaperDTO dto = new StandardExamPaperDTO();
        dto.setPaperId(paper.getPaperId());
        dto.setPaperName(paper.getPaperName());
        dto.setExamType(paper.getExamType());
        dto.setCreatedTime(paper.getCreatedTime());
        dto.setAuditStatus(paper.getAuditStatus());
        dto.setAuditComment(paper.getAuditComment());

        if (paper.getAuditedBy() != null) {
            dto.setAuditedByName(paper.getAuditedBy().getUsername());
        }

        dto.setPaperContentJson(paper.getPaperContent()); // ✅ 添加题目内容
        dto.setObjectiveAnswersJson(paper.getObjectiveAnswersJson()); // ✅ 添加客观题答案

        return dto;
    }
    @Override
    public void auditPaper(ExamPaperAuditDTO dto) {
        StandardExamPaper paper = standardExamPaperRepository.findById(dto.getPaperId())
                .orElseThrow(() -> new RuntimeException("试卷不存在"));

        paper.setAuditStatus(dto.getAuditStatus());
        paper.setAuditComment(dto.getAuditComment());

        User teacher = userRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new RuntimeException("教师不存在"));
        paper.setAuditedBy(teacher);

        standardExamPaperRepository.save(paper);
    }
}
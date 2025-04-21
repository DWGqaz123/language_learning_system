package com.hdu.language_learning_system.exam.service.impl;

import com.hdu.language_learning_system.exam.dto.MockExamCreateDTO;
import com.hdu.language_learning_system.exam.entity.MockExam;
import com.hdu.language_learning_system.exam.entity.StandardExamPaper;
import com.hdu.language_learning_system.exam.repository.MockExamRepository;
import com.hdu.language_learning_system.exam.repository.StandardExamPaperRepository;
import com.hdu.language_learning_system.studyRoom.entity.StudyRoom;
import com.hdu.language_learning_system.studyRoom.repository.StudyRoomRepository;
import com.hdu.language_learning_system.exam.service.MockExamService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MockExamServiceImpl implements MockExamService {

    @Resource
    private MockExamRepository mockExamRepository;

    @Resource
    private StandardExamPaperRepository standardExamPaperRepository;

    @Resource
    private StudyRoomRepository studyRoomRepository;

    @Override
    public void createMockExam(MockExamCreateDTO dto) {
        MockExam exam = new MockExam();
        exam.setExamName(dto.getExamName());
        exam.setExamTime(dto.getExamTime());

        StandardExamPaper paper = standardExamPaperRepository.findById(dto.getStandardPaperId())
                .orElseThrow(() -> new RuntimeException("试卷不存在"));
        exam.setStandardPaper(paper);

        StudyRoom room = studyRoomRepository.findById(dto.getExamRoomId())
                .orElseThrow(() -> new RuntimeException("考场不存在"));
        exam.setExamRoom(room);

        mockExamRepository.save(exam);
    }
}
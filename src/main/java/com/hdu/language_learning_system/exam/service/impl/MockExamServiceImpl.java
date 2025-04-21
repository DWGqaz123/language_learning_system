package com.hdu.language_learning_system.exam.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdu.language_learning_system.exam.dto.*;
import com.hdu.language_learning_system.exam.entity.MockExam;
import com.hdu.language_learning_system.exam.entity.StandardExamPaper;
import com.hdu.language_learning_system.exam.entity.StudentExamRecord;
import com.hdu.language_learning_system.exam.entity.StudentExamRecordId;
import com.hdu.language_learning_system.exam.repository.MockExamRepository;
import com.hdu.language_learning_system.exam.repository.StandardExamPaperRepository;
import com.hdu.language_learning_system.exam.repository.StudentExamRecordRepository;
import com.hdu.language_learning_system.notification.dto.NotificationDTO;
import com.hdu.language_learning_system.notification.service.NotificationService;
import com.hdu.language_learning_system.studyRoom.entity.StudyRoom;
import com.hdu.language_learning_system.studyRoom.repository.StudyRoomRepository;
import com.hdu.language_learning_system.exam.service.MockExamService;
import com.hdu.language_learning_system.user.entity.User;
import com.hdu.language_learning_system.user.repository.UserRepository;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class MockExamServiceImpl implements MockExamService {

    @Resource
    private MockExamRepository mockExamRepository;

    @Resource
    private StandardExamPaperRepository standardExamPaperRepository;

    @Resource
    private StudyRoomRepository studyRoomRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private StudentExamRecordRepository studentExamRecordRepository;

    @Resource
    private NotificationService notificationService;



    //添加模拟考试
    @Override
    public void createMockExam(MockExamCreateDTO dto) {
        MockExam exam = new MockExam();
        exam.setExamName(dto.getExamName());
        exam.setExamTime(dto.getExamTime());

        // 绑定试卷
        StandardExamPaper paper = standardExamPaperRepository.findById(dto.getStandardPaperId())
                .orElseThrow(() -> new RuntimeException("试卷不存在"));
        exam.setStandardPaper(paper);

        // 绑定考场
        StudyRoom room = studyRoomRepository.findById(dto.getExamRoomId())
                .orElseThrow(() -> new RuntimeException("考场不存在"));
        exam.setExamRoom(room);

        mockExamRepository.save(exam);
    }

    //添加学员到模拟考试
    @Override
    public void addStudentsToExam(ExamStudentAddDTO dto) {
        MockExam exam = mockExamRepository.findById(dto.getExamId())
                .orElseThrow(() -> new RuntimeException("模拟考试不存在"));

        for (Integer studentId : dto.getStudentIds()) {
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("学员不存在，ID: " + studentId));

            // 检查是否已存在记录
            boolean alreadyExists = studentExamRecordRepository
                    .findByStudent_UserIdAndExam_ExamId(studentId, exam.getExamId())
                    .isPresent();
            if (!alreadyExists) {
                // 创建考试记录并初始化主键
                StudentExamRecord record = new StudentExamRecord();
                StudentExamRecordId id = new StudentExamRecordId();
                id.setExamId(exam.getExamId());
                id.setStudentId(student.getUserId());
                record.setId(id);
                record.setExam(exam);
                record.setStudent(student);

                studentExamRecordRepository.save(record); // ❗保存记录

                // 发送考试通知
                NotificationDTO notify = new NotificationDTO();
                notify.setReceiverId(studentId);
                notify.setNotificationType("考试通知");
                notify.setRefTargetId(exam.getExamId());
                notify.setRefTargetType("模拟考试");
                notify.setContent("您有一场模拟考试安排在：" + exam.getExamTime() + "，请及时参加。");
                notificationService.sendNotification(notify);
            }
        }
    }

    //助教查看所有模拟考试
    @Override
    public List<MockExamBriefDTO> getAllMockExams() {
        List<MockExam> exams = mockExamRepository.findAll();

        return exams.stream().map(exam -> {
            MockExamBriefDTO dto = new MockExamBriefDTO();
            dto.setExamId(exam.getExamId());
            dto.setExamName(exam.getExamName());
            dto.setExamTime(exam.getExamTime());

            if (exam.getExamRoom() != null) {
                dto.setExamRoomName(exam.getExamRoom().getRoomName());
            }
            if (exam.getStandardPaper() != null) {
                dto.setPaperName(exam.getStandardPaper().getPaperName());
            }

            return dto;
        }).toList();
    }

    //学员查看自己的考试列表
    @Override
    public List<StudentExamRecordDTO> getStudentExamRecords(Integer studentId) {
        List<StudentExamRecord> records = studentExamRecordRepository.findByStudent_UserId(studentId);
        return records.stream().map(record -> {
            StudentExamRecordDTO dto = new StudentExamRecordDTO();
            dto.setExamId(record.getExam().getExamId());
            dto.setExamName(record.getExam().getExamName());
            dto.setExamTime(record.getExam().getExamTime());
            dto.setTotalScore(record.getTotalScore());
            dto.setSubjectiveScore(record.getSubjectiveScore());
            dto.setCompletedTime(record.getCompletedTime());
            dto.setTeacherComment(record.getTeacherComment());
            dto.setAssistantComment(record.getAssistantComment());
            return dto;
        }).toList();
    }

    //查看考试详情
    @Override
    public MockExamDetailDTO getExamDetail(Integer examId, Integer studentId) {
        MockExam exam = mockExamRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));

        MockExamDetailDTO dto = new MockExamDetailDTO();
        dto.setExamId(exam.getExamId());
        dto.setExamName(exam.getExamName());
        dto.setExamTime(exam.getExamTime());

        if (exam.getStandardPaper() != null) {
            dto.setPaperName(exam.getStandardPaper().getPaperName());
        }

        if (exam.getExamRoom() != null) {
            dto.setExamRoomName(exam.getExamRoom().getRoomName());
            dto.setLocation(exam.getExamRoom().getLocation());
        }

        // 查找学生考试记录（可选）
        if (studentId != null) {
            studentExamRecordRepository.findByStudent_UserIdAndExam_ExamId(studentId, examId)
                    .ifPresent(record -> {
                        dto.setTotalScore(record.getTotalScore());
                        dto.setSubjectiveScore(record.getSubjectiveScore());
                        dto.setTeacherComment(record.getTeacherComment());
                        dto.setAssistantComment(record.getAssistantComment());
                        dto.setCompletedTime(record.getCompletedTime());
                    });
        }

        return dto;
    }

    //学员查看试卷
    @Override
    public ExamPaperContentDTO getExamPaperContent(Integer examId, Integer studentId) {
        // 1. 查询考试记录，确保 studentId 和 examId 有绑定记录
        StudentExamRecord record = studentExamRecordRepository
                .findByStudent_UserIdAndExam_ExamId(studentId, examId)
                .orElseThrow(() -> new RuntimeException("未找到该考试记录"));

        // 2. 获取试卷内容
        StandardExamPaper paper = record.getExam().getStandardPaper();

        // 3. 封装返回数据
        ExamPaperContentDTO dto = new ExamPaperContentDTO();
        dto.setPaperName(paper.getPaperName());
        dto.setExamType(paper.getExamType());
        dto.setPaperContentJson(paper.getPaperContent());

        return dto;
    }

    //学员提交考试答案
    @Override
    public void submitExamAnswers(ExamAnswerSubmitDTO dto) {
        // 1. 查找考试记录
        StudentExamRecord record = studentExamRecordRepository
                .findByStudent_UserIdAndExam_ExamId(dto.getStudentId(), dto.getExamId())
                .orElseThrow(() -> new RuntimeException("未找到考试记录"));

        // 2. 设置答题内容和完成时间
        record.setAnswersJson(dto.getAnswersJson());
        record.setCompletedTime(new Timestamp(System.currentTimeMillis()));
        System.out.println("设置完成时间：" + record.getCompletedTime());
        record.setExamStatus("submitted");
        studentExamRecordRepository.save(record);
        studentExamRecordRepository.flush(); // 强制写入数据库
    }

    //系统批改客观题
    @Override
    public void autoGradeObjectiveQuestions(Integer examId, Integer studentId) {
        // 获取考试记录
        StudentExamRecord record = studentExamRecordRepository
                .findByStudent_UserIdAndExam_ExamId(studentId, examId)
                .orElseThrow(() -> new RuntimeException("未找到考试记录"));

        // 获取试卷
        StandardExamPaper paper = record.getExam().getStandardPaper();

        // 获取标准答案和学员答案（均为 JSON 字符串）
        String correctAnswersJson = paper.getObjectiveAnswersJson();
        String studentAnswersJson = record.getAnswersJson();

        if (correctAnswersJson == null || studentAnswersJson == null) {
            throw new RuntimeException("标准答案或学员答案为空，无法批改");
        }

        // 使用 ObjectMapper 解析 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<StudentAnswerDTO> correctAnswers = objectMapper.readValue(
                    correctAnswersJson,
                    new TypeReference<List<StudentAnswerDTO>>() {}
            );

            List<StudentAnswerDTO> studentAnswers = objectMapper.readValue(
                    studentAnswersJson,
                    new TypeReference<List<StudentAnswerDTO>>() {}
            );

            // 自动比对
            int score = 0;
            for (StudentAnswerDTO correct : correctAnswers) {
                for (StudentAnswerDTO student : studentAnswers) {
                    if (correct.getQuestionId().equals(student.getQuestionId()) &&
                            correct.getAnswer().equalsIgnoreCase(student.getAnswer())) {
                        score += 1; // 每题 1 分，后期可改成从 questionDTO 获取分值
                    }
                }
            }

            // 设置成绩并保存
            record.setObjectiveScore(score);
            record.setExamStatus("graded");
            studentExamRecordRepository.save(record);

        } catch (Exception e) {
            throw new RuntimeException("客观题批改失败：" + e.getMessage(), e);
        }
    }

    //删除模拟考试
    @Override
    @Transactional
    public void deleteMockExamById(Integer examId) {
        // 检查是否存在
        MockExam exam = mockExamRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("该模拟考试不存在"));

        // 检查是否已有学员记录该考试，防止误删
        boolean hasStudentRecord = studentExamRecordRepository.findByExam_ExamId(examId).size() > 0;
        if (hasStudentRecord) {
            throw new RuntimeException("该模拟考试已有关联的学员记录，无法删除");
        }

        mockExamRepository.deleteById(examId);
    }

}
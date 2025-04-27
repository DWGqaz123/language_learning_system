package com.hdu.language_learning_system.exam.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdu.language_learning_system.course.entity.Schedule;
import com.hdu.language_learning_system.course.repository.ScheduleRepository;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Resource
    private ScheduleRepository scheduleRepository;



    //添加模拟考试
    @Override
    public Integer createMockExam(MockExamCreateDTO dto) {
        Timestamp examTime = dto.getExamTime();

        // 判断是否有课程或考试冲突
        Integer roomId = dto.getExamRoomId();
        LocalDate examDate = examTime.toLocalDateTime().toLocalDate();

        List<Schedule> conflictingSchedules = scheduleRepository.findByRoom_RoomIdAndClassTimeBetween(
                roomId,
                Timestamp.valueOf(examDate.atStartOfDay()),
                Timestamp.valueOf(examDate.plusDays(1).atStartOfDay())
        );
        if (!conflictingSchedules.isEmpty()) {
            throw new RuntimeException("该考场当天已有课程安排，无法安排考试");
        }

        List<MockExam> conflictingExams = mockExamRepository.findByExamRoom_RoomIdAndExamTimeBetween(
                roomId,
                Timestamp.valueOf(examDate.atStartOfDay()),
                Timestamp.valueOf(examDate.plusDays(1).atStartOfDay())
        );
        if (!conflictingExams.isEmpty()) {
            throw new RuntimeException("该考场当天已有其他考试安排，无法安排考试");
        }

        // 无冲突，继续创建考试
        MockExam exam = new MockExam();
        exam.setExamName(dto.getExamName());
        exam.setExamTime(examTime);

        StandardExamPaper paper = standardExamPaperRepository.findById(dto.getStandardPaperId())
                .orElseThrow(() -> new RuntimeException("试卷不存在"));
        exam.setStandardPaper(paper);

        StudyRoom room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("考场不存在"));
        exam.setExamRoom(room);

        // 保存并返回 examId
        MockExam savedExam = mockExamRepository.save(exam);
        return savedExam.getExamId();
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
            dto.setExamStatus(record.getExamStatus());
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
            studentExamRecordRepository.save(record);

        } catch (Exception e) {
            throw new RuntimeException("客观题批改失败：" + e.getMessage(), e);
        }
    }
    //教师批改主观题
    @Override
    @Transactional
    public void gradeSubjectiveQuestion(SubjectiveScoreDTO dto) {
        StudentExamRecord record = studentExamRecordRepository
                .findByStudent_UserIdAndExam_ExamId(dto.getStudentId(), dto.getExamId())
                .orElseThrow(() -> new RuntimeException("未找到考试记录"));

        record.setSubjectiveScore(dto.getSubjectiveScore());
        record.setTeacherComment(dto.getTeacherComment());

        //计算总分
        Integer objScore = record.getObjectiveScore() != null ? record.getObjectiveScore() : 0;
        Integer subjScore = dto.getSubjectiveScore() != null ? dto.getSubjectiveScore() : 0;
        record.setTotalScore(objScore + subjScore);
        record.setExamStatus("graded");
        studentExamRecordRepository.save(record);
    }

    // 查看学员答卷详情
    @Override
    public StudentExamDetailDTO getStudentExamDetail(Integer examId, Integer studentId) {
        StudentExamRecord record = studentExamRecordRepository
                .findByStudent_UserIdAndExam_ExamId(studentId, examId)
                .orElseThrow(() -> new RuntimeException("未找到考试记录"));

        MockExam exam = record.getExam();
        StandardExamPaper paper = exam.getStandardPaper();

        StudentExamDetailDTO dto = new StudentExamDetailDTO();
        dto.setExamId(examId);
        dto.setStudentId(studentId);
        dto.setExamName(exam.getExamName());
        dto.setExamType(paper.getExamType());
        dto.setExamTime(exam.getExamTime());

        dto.setAnswersJson(record.getAnswersJson());
        dto.setObjectiveScore(record.getObjectiveScore());
        dto.setSubjectiveScore(record.getSubjectiveScore());
        dto.setTotalScore(record.getTotalScore());
        dto.setTeacherComment(record.getTeacherComment());
        dto.setAssistantComment(record.getAssistantComment());
        dto.setCompletedTime(record.getCompletedTime());

        // ✅ 额外：提取主观题内容
        String paperContentJson = paper.getPaperContent();
        if (paperContentJson != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<QuestionDTO> questions = objectMapper.readValue(
                        paperContentJson,
                        new TypeReference<List<QuestionDTO>>() {}
                );

                // 只保留主观题（type = subjective）
                List<QuestionDTO> subjectiveQuestions = questions.stream()
                        .filter(q -> "subjective".equalsIgnoreCase(q.getType()))
                        .collect(Collectors.toList());

                // 转回 JSON 字符串返回
                String subjectiveQuestionsJson = objectMapper.writeValueAsString(subjectiveQuestions);
                dto.setSubjectiveQuestionsJson(subjectiveQuestionsJson);

            } catch (Exception e) {
                throw new RuntimeException("解析试卷内容失败：" + e.getMessage(), e);
            }
        }

        return dto;
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


    //更新模拟考试
    @Override
    @Transactional
    public void updateMockExam(MockExamUpdateDTO dto) {
        MockExam exam = mockExamRepository.findById(dto.getExamId())
                .orElseThrow(() -> new RuntimeException("模拟考试不存在"));

        // 判断是否修改了考场或时间，才做冲突校验
        boolean roomChanged = dto.getExamRoomId() != null && !dto.getExamRoomId().equals(exam.getExamRoom().getRoomId());
        boolean timeChanged = dto.getExamTime() != null && !dto.getExamTime().equals(exam.getExamTime());

        if (roomChanged || timeChanged) {
            Integer newRoomId = dto.getExamRoomId() != null ? dto.getExamRoomId() : exam.getExamRoom().getRoomId();
            Timestamp newExamTime = dto.getExamTime() != null ? dto.getExamTime() : exam.getExamTime();

            LocalDate examDate = newExamTime.toLocalDateTime().toLocalDate();

            // 检查课程冲突
            List<Schedule> conflictingSchedules = scheduleRepository.findByRoom_RoomIdAndClassTimeBetween(
                    newRoomId,
                    Timestamp.valueOf(examDate.atStartOfDay()),
                    Timestamp.valueOf(examDate.plusDays(1).atStartOfDay())
            );
            for (Schedule s : conflictingSchedules) {
                if (!s.getScheduleId().equals(exam.getExamId())) {
                    throw new RuntimeException("该考场当天已有课程安排，无法修改");
                }
            }

            // 检查考试冲突（排除当前考试）
            List<MockExam> conflictingExams = mockExamRepository.findByExamRoom_RoomIdAndExamTimeBetween(
                    newRoomId,
                    Timestamp.valueOf(examDate.atStartOfDay()),
                    Timestamp.valueOf(examDate.plusDays(1).atStartOfDay())
            );
            for (MockExam e : conflictingExams) {
                if (!e.getExamId().equals(dto.getExamId())) {
                    throw new RuntimeException("该考场当天已有其他考试安排，无法修改");
                }
            }
        }

        // 更新字段
        if (dto.getExamName() != null) {
            exam.setExamName(dto.getExamName());
        }

        if (dto.getExamTime() != null) {
            exam.setExamTime(dto.getExamTime());
        }

        if (dto.getStandardPaperId() != null) {
            StandardExamPaper paper = standardExamPaperRepository.findById(dto.getStandardPaperId())
                    .orElseThrow(() -> new RuntimeException("试卷不存在"));
            exam.setStandardPaper(paper);
        }

        if (dto.getExamRoomId() != null) {
            StudyRoom room = studyRoomRepository.findById(dto.getExamRoomId())
                    .orElseThrow(() -> new RuntimeException("教室不存在"));
            exam.setExamRoom(room);
        }

        mockExamRepository.save(exam);
    }

    //助教评价考试
    @Override
    @Transactional
    public void submitAssistantComment(AssistantCommentDTO dto) {
        StudentExamRecord record = studentExamRecordRepository
                .findByStudent_UserIdAndExam_ExamId(dto.getStudentId(), dto.getExamId())
                .orElseThrow(() -> new RuntimeException("考试记录不存在"));

        record.setAssistantComment(dto.getAssistantComment());
        record.setExamStatus("ended");
        studentExamRecordRepository.save(record);
    }

    //查看考试成绩报告
    @Override
    public ExamReportDTO getExamReport(Integer examId, Integer studentId) {
        StudentExamRecord record = studentExamRecordRepository
                .findByStudent_UserIdAndExam_ExamId(studentId, examId)
                .orElseThrow(() -> new RuntimeException("未找到考试记录"));

        MockExam exam = record.getExam();
        StandardExamPaper paper = exam.getStandardPaper();

        ExamReportDTO dto = new ExamReportDTO();
        dto.setExamId(examId);
        dto.setExamName(exam.getExamName());
        dto.setExamTime(exam.getExamTime());
        dto.setPaperName(paper.getPaperName());
        dto.setExamRoomName(exam.getExamRoom().getRoomName());

        dto.setObjectiveScore(record.getObjectiveScore());
        dto.setSubjectiveScore(record.getSubjectiveScore());
        dto.setTotalScore(record.getTotalScore());
        dto.setTeacherComment(record.getTeacherComment());
        dto.setAssistantComment(record.getAssistantComment());
        dto.setCompletedTime(record.getCompletedTime());

        // 解析 answers_json
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<StudentAnswerDTO> answers = objectMapper.readValue(
                    record.getAnswersJson(),
                    new com.fasterxml.jackson.core.type.TypeReference<List<StudentAnswerDTO>>() {}
            );
            dto.setAnswers(answers);
        } catch (Exception e) {
            throw new RuntimeException("解析答题信息失败：" + e.getMessage());
        }

        return dto;
    }

    //查看某次考试下的所有学员考试记录
    @Override
    public List<StudentExamRecordDTO> getRecordsByExamId(Integer examId) {
        List<StudentExamRecord> records = studentExamRecordRepository.findByExam_ExamId(examId);

        return records.stream().map(record -> {
            StudentExamRecordDTO dto = new StudentExamRecordDTO();
            dto.setStudentId(record.getStudent().getUserId());
            dto.setExamId(record.getExam().getExamId());
            dto.setTotalScore(record.getTotalScore());
            dto.setSubjectiveScore(record.getSubjectiveScore());
            dto.setTeacherComment(record.getTeacherComment());
            dto.setAssistantComment(record.getAssistantComment());
            dto.setCompletedTime(record.getCompletedTime());
            dto.setAnswers(record.getAnswersJson()); // 如果是 Map 转 JSON 的字段则转为字符串
            dto.setExamName(record.getExam().getExamName());
            dto.setExamTime(record.getExam().getExamTime());
            return dto;
        }).collect(Collectors.toList());
    }

    //查看所有带批改试卷
    @Override
    public List<UnGradedExamRecordDTO> getUnGradedSubjectiveExamRecords() {
        // 查询考试记录中状态为 "submitted" 的
        List<StudentExamRecord> records = studentExamRecordRepository.findByExamStatus("submitted");

        List<UnGradedExamRecordDTO> result = new ArrayList<>();
        for (StudentExamRecord r : records) {
            UnGradedExamRecordDTO dto = new UnGradedExamRecordDTO();

            dto.setStudentId(r.getStudent().getUserId());
            dto.setStudentName(r.getStudent().getUsername());
            dto.setExamId(r.getExam().getExamId());
            dto.setExamName(r.getExam().getExamName());
            dto.setExamTime(r.getExam().getExamTime());
            dto.setAnswersJson(r.getAnswersJson());

            // 试卷内容
            if (r.getExam().getStandardPaper() != null) {
                dto.setPaperContentJson(r.getExam().getStandardPaper().getPaperContent());
            }

            result.add(dto);
        }
        return result;
    }
}
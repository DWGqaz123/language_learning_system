package com.hdu.language_learning_system.analysis.service.impl;

import com.hdu.language_learning_system.analysis.dto.*;
import com.hdu.language_learning_system.analysis.entity.StudentPerformanceReport;
import com.hdu.language_learning_system.analysis.repository.StudentPerformanceReportRepository;
import com.hdu.language_learning_system.common.ApiResponse;
import com.hdu.language_learning_system.course.entity.Course;
import com.hdu.language_learning_system.course.entity.StudentScheduleRecord;
import com.hdu.language_learning_system.course.repository.CourseRepository;
import com.hdu.language_learning_system.course.repository.StudentScheduleRecordRepository;
import com.hdu.language_learning_system.exam.entity.StudentExamRecord;
import com.hdu.language_learning_system.exam.repository.StudentExamRecordRepository;
import com.hdu.language_learning_system.studyRoom.dto.StudyRoomUsageStatisticsDTO;
import com.hdu.language_learning_system.studyRoom.entity.StudyRoomReservation;
import com.hdu.language_learning_system.studyRoom.repository.StudyRoomReservationRepository;
import com.hdu.language_learning_system.task.entity.TaskAssignment;
import com.hdu.language_learning_system.task.repository.TaskAssignmentRepository;
import com.hdu.language_learning_system.user.entity.User;
import com.hdu.language_learning_system.user.repository.UserRepository;
import com.hdu.language_learning_system.analysis.service.AnalysisService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentScheduleRecordRepository studentScheduleRecordRepository;

    @Autowired
    private StudentScheduleRecordRepository recordRepository;

    @Autowired
    private TaskAssignmentRepository taskAssignmentRepository;

    @Autowired
    private StudentExamRecordRepository studentExamRecordRepository;

    @Autowired
    private StudentPerformanceReportRepository studentPerformanceReportRepository;

    @Autowired
    private StudyRoomReservationRepository studyRoomReservationRepository;
    private StudentPerformanceReportDTO convertToDTO(StudentPerformanceReport report) {
        StudentPerformanceReportDTO dto = new StudentPerformanceReportDTO();
        dto.setReportId(report.getReportId());
        dto.setStudentId(report.getStudent().getUserId());
        dto.setAttendanceSummary(report.getAttendanceSummary());
        dto.setTaskSummary(report.getTaskSummary());
        dto.setExamSummary(report.getExamSummary());
        dto.setStudyRoomSummary(report.getStudyRoomSummary());
        dto.setOverallScore(report.getOverallScore());
        dto.setAssistantComment(report.getAssistantComment());
        dto.setGeneratedTime(report.getGeneratedTime());
        return dto;
    }
    @Override
    public CourseProgressDTO getCourseProgress(Integer studentId) {
        try {
            if (studentId == null) {
                throw new RuntimeException("studentId不能为空");
            }

            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("学员不存在，ID：" + studentId));

            Set<Course> allCourses = new HashSet<>();

            // 添加 1对1 课程
            List<Course> personalCourses = courseRepository.findByStudent(student);
            if (personalCourses != null && !personalCourses.isEmpty()) {
                allCourses.addAll(new ArrayList<>(personalCourses));
            }

            // 添加班级课程
            List<StudentScheduleRecord> records = studentScheduleRecordRepository.findByStudent(student);
            if (records != null && !records.isEmpty()) {
                for (StudentScheduleRecord record : records) {
                    if (record != null && record.getCourse() != null) {
                        allCourses.add(record.getCourse());
                    }
                }
            }

            int total = 0;
            int remaining = 0;
            for (Course c : allCourses) {
                if (c != null) {
                    if (c.getTotalHours() != null) {
                        total += c.getTotalHours();
                    }
                    if (c.getRemainingHours() != null) {
                        remaining += c.getRemainingHours();
                    }
                }
            }

            int completed = total - remaining; // ✅ 计算完成课时

            int progress = (total == 0) ? 0 : (int) Math.round((1.0 * completed / total) * 100);

            CourseProgressDTO dto = new CourseProgressDTO();
            dto.setStudentId(studentId);
            dto.setTotalHours(total);
            dto.setRemainingHours(remaining);
            dto.setCompletedHours(completed); // ✅ 补充这行
            dto.setCompletedPercentage(progress);

            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取课程进度失败：" + (e.getMessage() != null ? e.getMessage() : "系统内部异常"));
        }
    }
    //课程考勤统计
    @Override
    public AttendanceStatsDTO getAttendanceStats(Integer studentId) {
        List<StudentScheduleRecord> records = recordRepository.findByStudent_UserId(studentId);

        AttendanceStatsDTO dto = new AttendanceStatsDTO();
        dto.setStudentId(studentId);
        dto.setTotalRecords(records.size());

        int attend = 0, absent = 0, leave = 0;
        for (StudentScheduleRecord r : records) {
            switch (r.getAttendStatus()) {
                case "出勤" -> attend++;
                case "缺勤" -> absent++;
                case "请假" -> leave++;
            }
        }

        dto.setAttendCount(attend);
        dto.setAbsentCount(absent);
        dto.setLeaveCount(leave);

        return dto;
    }

    //任务数据统计（完成率、平均分）
    @Override
    public TaskStatisticsDTO getTaskStatistics(Integer studentId) {
        List<TaskAssignment> allAssignments = taskAssignmentRepository.findByStudent_UserId(studentId);

        int total = allAssignments.size();
        int submitted = 0;
        double totalScore = 0;
        int scoredCount = 0;

        for (TaskAssignment t : allAssignments) {
            if (t.getSubmitTime() != null) {
                submitted++;
            }
            if (t.getScore() != null) {
                totalScore += t.getScore();
                scoredCount++;
            }
        }

        TaskStatisticsDTO dto = new TaskStatisticsDTO();
        dto.setStudentId(studentId);
        dto.setTotalTasks(total);
        dto.setSubmittedTasks(submitted);
        dto.setCompletionRate(total == 0 ? 0.0 : (submitted * 100.0 / total));
        dto.setAverageScore(scoredCount == 0 ? null : totalScore / scoredCount);

        return dto;
    }

    //模拟考试统计总体
    @Override
    public StudentMockExamStatSimpleDTO getMockExamStatByStudentId(Integer studentId) {
        try {
            List<StudentExamRecord> records = studentExamRecordRepository.findByStudent_UserId(studentId);

            if (records == null || records.isEmpty()) {
                // 如果找不到记录，也要返回一个空对象，防止后续空指针
                StudentMockExamStatSimpleDTO dto = new StudentMockExamStatSimpleDTO();
                dto.setStudentId(studentId);
                dto.setExamCount(0);
                dto.setAverageScore(0.0);
                return dto;
            }

            int count = records.size();
            double average = records.stream()
                    .mapToDouble(r -> r.getTotalScore() != null ? r.getTotalScore() : 0)
                    .average()
                    .orElse(0.0);

            StudentMockExamStatSimpleDTO dto = new StudentMockExamStatSimpleDTO();
            dto.setStudentId(studentId);
            dto.setExamCount(count);
            dto.setAverageScore(Math.round(average * 10.0) / 10.0);

            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查询模拟考试统计失败：" + (e.getMessage() != null ? e.getMessage() : "未知错误"));
        }
    }
    //模拟考试统计具体

    @Override
    public List<ExamScoreTrendDTO> getExamScoreTrend(Integer studentId) {
        List<StudentExamRecord> records = studentExamRecordRepository.findByStudent_UserId(studentId);

        List<ExamScoreTrendDTO> result = new ArrayList<>();

        for (StudentExamRecord record : records) {
            if (record.getExam() != null && record.getTotalScore() != null) {
                ExamScoreTrendDTO dto = new ExamScoreTrendDTO();
                dto.setExamName(record.getExam().getExamName());
                dto.setScore(record.getTotalScore());
                result.add(dto);
            }
        }

        return result;
    }

    //更新助教点评
    @Override
    public void updateAssistantComment(PerformanceCommentDTO dto) {
        StudentPerformanceReport report = studentPerformanceReportRepository.findById(dto.getReportId())
                .orElseThrow(() -> new RuntimeException("报告不存在"));

        report.setAssistantComment(dto.getAssistantComment());
        studentPerformanceReportRepository.save(report);
    }

    //查看学员报告
    @Override
    public StudentPerformanceReportDTO getReportById(Integer reportId) {
        StudentPerformanceReport report = studentPerformanceReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("报告不存在"));
        return convertToDTO(report);
    }


    //获取所有报告
    @Override
    public List<StudentPerformanceReportDTO> getAllReports() {
        return studentPerformanceReportRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    //产生报告
    @Override
    @Transactional
    public void generatePerformanceReport(GenerateReportRequestDTO request) {
        Integer studentId = request.getStudentId();
        if (studentId == null) {
            throw new RuntimeException("学员ID不能为空");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学员不存在，ID：" + studentId));

        // ---------- 出勤情况 ----------
        List<StudentScheduleRecord> records = studentScheduleRecordRepository.findByStudent_UserId(studentId);
        int total = records.size(), attend = 0, absent = 0, leave = 0;
        for (StudentScheduleRecord r : records) {
            String status = r.getAttendStatus();
            if (status == null) continue;
            switch (status) {
                case "出勤" -> attend++;
                case "缺勤" -> absent++;
                case "请假" -> leave++;
            }
        }
        String attendanceSummary = String.format("共 %d 次记录，出勤 %d 次，缺勤 %d 次，请假 %d 次。",
                total, attend, absent, leave);

        // ---------- 任务完成情况 ----------
        List<TaskAssignment> tasks = taskAssignmentRepository.findByStudent_UserId(studentId);
        int totalTasks = tasks.size(), submitted = 0;
        double scoreSum = 0;
        int scoredCount = 0;
        for (TaskAssignment t : tasks) {
            if (t.getSubmitTime() != null) submitted++;
            if (t.getScore() != null) {
                scoreSum += t.getScore();
                scoredCount++;
            }
        }
        String taskSummary = String.format("共 %d 项任务，提交 %d 项，完成率 %.1f%%，平均分 %s。",
                totalTasks, submitted,
                totalTasks == 0 ? 0.0 : submitted * 100.0 / totalTasks,
                scoredCount == 0 ? "暂无" : String.format("%.1f", scoreSum / scoredCount));

        // ---------- 模拟考试情况 ----------
        List<StudentExamRecord> exams = studentExamRecordRepository.findByStudent_UserId(studentId);
        double avg = exams.stream()
                .map(StudentExamRecord::getTotalScore)
                .filter(Objects::nonNull)
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(0.0);
        String examSummary = String.format("共参加 %d 次考试，平均得分 %.1f 分。", exams.size(), avg);

        // ---------- 自习室使用情况 ----------
        List<StudyRoomReservation> reservations = studyRoomReservationRepository.findByStudent_UserId(studentId);
        int totalUsage = 0, morning = 0, afternoon = 0, evening = 0;
        for (StudyRoomReservation r : reservations) {
            if (!"通过".equals(r.getReviewStatus())) continue;
            if (r.getSignInTime() == null && r.getSignOutTime() == null) continue;

            totalUsage++;
            String slot = r.getTimeSlot();
            if (slot == null) continue;
            switch (slot) {
                case "上午" -> morning++;
                case "下午" -> afternoon++;
                case "晚上" -> evening++;
            }
        }
        String studyRoomSummary = String.format("共使用 %d 次自习室，上午 %d 次，下午 %d 次，晚上 %d 次。",
                totalUsage, morning, afternoon, evening);

        // ---------- 构建并保存报告 ----------
        StudentPerformanceReport report = new StudentPerformanceReport();
        report.setStudent(student);
        report.setAttendanceSummary(attendanceSummary);
        report.setTaskSummary(taskSummary);
        report.setExamSummary(examSummary);
        report.setStudyRoomSummary(studyRoomSummary);

        report.setOverallScore(request.getOverallScore() != null ? request.getOverallScore() : 0);

        // ✅ 新增保存助教点评 assistant_comment
        report.setAssistantComment(request.getAssistantComment());

        report.setGeneratedTime(new Timestamp(System.currentTimeMillis()));

        studentPerformanceReportRepository.save(report);
    }

    //查询某学员的所有报告
    @Override
    public List<StudentPerformanceReportDTO> getReportsByStudentId(Integer studentId) {
        List<StudentPerformanceReport> reports = studentPerformanceReportRepository.findByStudent_UserId(studentId);

        return reports.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    //查看学员最新报告
    // AnalysisServiceImpl.java
    @Override
    public ApiResponse<StudentPerformanceReportDTO> getLatestReportByStudentId(Integer studentId) {
        List<StudentPerformanceReport> reports = studentPerformanceReportRepository.findByStudent_UserIdOrderByGeneratedTimeDesc(studentId);

        if (reports.isEmpty()) {
            return ApiResponse.error("未找到学习报告");
        }

        StudentPerformanceReport latestReport = reports.get(0);

        StudentPerformanceReportDTO dto = new StudentPerformanceReportDTO();
        dto.setReportId(latestReport.getReportId());
        dto.setStudentId(latestReport.getStudent().getUserId());
        dto.setAttendanceSummary(latestReport.getAttendanceSummary());
        dto.setTaskSummary(latestReport.getTaskSummary());
        dto.setExamSummary(latestReport.getExamSummary());
        dto.setStudyRoomSummary(latestReport.getStudyRoomSummary());
        dto.setOverallScore(latestReport.getOverallScore());
        dto.setAssistantComment(latestReport.getAssistantComment());
        dto.setGeneratedTime(latestReport.getGeneratedTime());

        return ApiResponse.success(dto);
    }

    private StudyRoomUsageStatisticsDTO getStudentUsageStatistics(Integer studentId) {
        List<StudyRoomReservation> records = studyRoomReservationRepository.findByStudent_UserId(studentId);

        int total = 0;
        int morning = 0, afternoon = 0, evening = 0;

        for (StudyRoomReservation record : records) {
            if (!"通过".equals(record.getReviewStatus())) continue;
            if (record.getSignInTime() == null && record.getSignOutTime() == null) continue;

            total++;
            switch (record.getTimeSlot()) {
                case "上午" -> morning++;
                case "下午" -> afternoon++;
                case "晚上" -> evening++;
            }
        }

        StudyRoomUsageStatisticsDTO dto = new StudyRoomUsageStatisticsDTO();
        dto.setTotalUsageCount(total);
        dto.setMorningCount(morning);
        dto.setAfternoonCount(afternoon);
        dto.setEveningCount(evening);

        return dto;
    }

    @Override
    public WeeklyAnalysisDTO getWeeklyAnalysis(Integer studentId) {
        WeeklyAnalysisDTO dto = new WeeklyAnalysisDTO();
        dto.setStudentId(studentId);
        dto.setCourseProgress(getCourseProgress(studentId));
        dto.setAttendanceStats(getAttendanceStats(studentId));
        dto.setTaskStats(getTaskStatistics(studentId));
        dto.setExamStats(getMockExamStatByStudentId(studentId));
        dto.setStudyRoomStats(getStudentUsageStatistics(studentId));
        return dto;
    }

    @Override
    public StageAnalysisDTO getStageAnalysis(Integer studentId) {
        StageAnalysisDTO dto = new StageAnalysisDTO();
        dto.setStudentId(studentId);
        dto.setCourseProgress(getCourseProgress(studentId));
        dto.setAttendanceStats(getAttendanceStats(studentId));
        dto.setTaskStats(getTaskStatistics(studentId));
        dto.setExamStats(getMockExamStatByStudentId(studentId));
        dto.setStudyRoomStats(getStudentUsageStatistics(studentId));
        dto.setExamScoreTrend(getExamScoreTrend(studentId));
        return dto;
    }

}
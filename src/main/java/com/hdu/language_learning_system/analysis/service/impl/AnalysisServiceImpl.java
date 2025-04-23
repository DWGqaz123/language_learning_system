package com.hdu.language_learning_system.analysis.service.impl;

import com.hdu.language_learning_system.analysis.dto.*;
import com.hdu.language_learning_system.course.entity.Course;
import com.hdu.language_learning_system.course.entity.StudentScheduleRecord;
import com.hdu.language_learning_system.course.repository.CourseRepository;
import com.hdu.language_learning_system.course.repository.StudentScheduleRecordRepository;
import com.hdu.language_learning_system.exam.entity.StudentExamRecord;
import com.hdu.language_learning_system.exam.repository.StudentExamRecordRepository;
import com.hdu.language_learning_system.task.entity.TaskAssignment;
import com.hdu.language_learning_system.task.repository.TaskAssignmentRepository;
import com.hdu.language_learning_system.user.entity.User;
import com.hdu.language_learning_system.user.repository.UserRepository;
import com.hdu.language_learning_system.analysis.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @Override
    public CourseProgressDTO getCourseProgress(Integer studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学员不存在"));

        Set<Course> allCourses = new HashSet<>();

        // 添加 1对1 课程
        List<Course> personalCourses = courseRepository.findByStudent(student);
        allCourses.addAll(personalCourses);

        // 添加班级课程
        List<StudentScheduleRecord> records = studentScheduleRecordRepository.findByStudent(student);
        for (StudentScheduleRecord record : records) {
            allCourses.add(record.getCourse());
        }

        int total = 0;
        int remaining = 0;
        for (Course c : allCourses) {
            if (c.getTotalHours() != null) total += c.getTotalHours();
            if (c.getRemainingHours() != null) remaining += c.getRemainingHours();
        }

        int progress = (total == 0) ? 0 : (int) Math.round((1.0 * (total - remaining) / total) * 100);

        CourseProgressDTO dto = new CourseProgressDTO();
        dto.setTotalHours(total);
        dto.setRemainingHours(remaining);
        dto.setCompletedPercentage(progress);

        return dto;
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

    //模拟考试统计
    @Override
    public StudentMockExamStatSimpleDTO getMockExamStatByStudentId(Integer studentId) {
        List<StudentExamRecord> records = studentExamRecordRepository.findByStudent_UserId(studentId);

        int count = records.size();
        double average = count == 0 ? 0.0 :
                records.stream()
                        .mapToDouble(StudentExamRecord::getTotalScore)
                        .average()
                        .orElse(0.0);

        StudentMockExamStatSimpleDTO dto = new StudentMockExamStatSimpleDTO();
        dto.setStudentId(studentId);
        dto.setExamCount(count);
        dto.setAverageScore(Math.round(average * 10.0) / 10.0); // 保留1位小数

        return dto;
    }
}
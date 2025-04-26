package com.hdu.language_learning_system.task.service.impl;

import com.hdu.language_learning_system.notification.entity.Notification;
import com.hdu.language_learning_system.notification.repository.NotificationRepository;
import com.hdu.language_learning_system.task.dto.*;
import com.hdu.language_learning_system.task.entity.*;
import com.hdu.language_learning_system.user.entity.User;
import com.hdu.language_learning_system.task.repository.TaskAssignmentRepository;
import com.hdu.language_learning_system.task.repository.TaskRepository;
import com.hdu.language_learning_system.task.service.*;
import com.hdu.language_learning_system.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
public class TaskAssignmentServiceImpl implements TaskAssignmentService {

    @Resource
    private TaskAssignmentRepository taskAssignmentRepository;

    @Resource
    private TaskRepository taskRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private NotificationRepository notificationRepository;

    //学员任务提交
    @Override
    public void submitTaskWithFile(TaskSubmissionDTO dto) {
        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        User student = userRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("学生不存在"));

        TaskAssignment assignment = taskAssignmentRepository
                .findByTask_TaskIdAndStudent_UserId(dto.getTaskId(), dto.getStudentId())
                .orElse(new TaskAssignment());

        assignment.setTask(task);
        assignment.setStudent(student);
        assignment.setCompletionStatus("已完成");
        assignment.setSubmitTime(new Timestamp(System.currentTimeMillis()));

        // 文件上传逻辑
        if (dto.getAttachmentFile() != null && !dto.getAttachmentFile().isEmpty()) {
            try {
                MultipartFile file = dto.getAttachmentFile();
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

                // 上传目录放在项目根路径 /uploads/assignments/
                String uploadDir = System.getProperty("user.dir") + "/uploads/assignments/";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(fileName);
                file.transferTo(filePath.toFile());

                assignment.setAttachmentPath("/uploads/assignments/" + fileName);
            } catch (IOException e) {
                throw new RuntimeException("文件上传失败: " + e.getMessage());
            }
        }

        if (dto.getSubmitText() != null) {
            assignment.setSubmitText(dto.getSubmitText());
        }

        taskAssignmentRepository.save(assignment);
    }
    //查看某学员的任务提交
    @Override
    public List<TaskSubmissionDTO> getSubmissionsByStudentId(Integer studentId) {
        List<TaskAssignment> assignments = taskAssignmentRepository.findByStudent_UserId(studentId);
        List<TaskSubmissionDTO> result = new ArrayList<>();

        for (TaskAssignment a : assignments) {
            TaskSubmissionDTO dto = new TaskSubmissionDTO();
            dto.setTaskId(a.getTask().getTaskId());
            dto.setTaskType(a.getTask().getTaskType());
            dto.setTaskContent(a.getTask().getTaskContent());
            dto.setPublishTime(a.getTask().getPublishTime());
            dto.setDeadline(a.getTask().getDeadline());
            dto.setCompletionStatus(a.getCompletionStatus());
            dto.setSubmitTime(a.getSubmitTime());
            dto.setAttachmentPath(a.getAttachmentPath());
            dto.setSubmitText(a.getSubmitText());
            dto.setScore(a.getScore());
            dto.setGradeComment(a.getGradeComment());
            result.add(dto);
        }

        return result;
    }

    //查看某个任务的所有提交记录
    @Override
    public List<TaskSubmissionDTO> getSubmissionsByTaskId(Integer taskId) {
        List<TaskAssignment> assignments = taskAssignmentRepository.findByTask_TaskId(taskId);
        List<TaskSubmissionDTO> result = new ArrayList<>();

        for (TaskAssignment a : assignments) {
            TaskSubmissionDTO dto = new TaskSubmissionDTO();
            dto.setTaskId(a.getTask().getTaskId());
            dto.setTaskType(a.getTask().getTaskType());
            dto.setTaskContent(a.getTask().getTaskContent());
            dto.setPublishTime(a.getTask().getPublishTime());
            dto.setDeadline(a.getTask().getDeadline());
            dto.setCompletionStatus(a.getCompletionStatus());
            dto.setSubmitTime(a.getSubmitTime());
            dto.setAttachmentPath(a.getAttachmentPath());
            dto.setSubmitText(a.getSubmitText());
            dto.setScore(a.getScore());
            dto.setGradeComment(a.getGradeComment());

            // 补充学员信息
            dto.setStudentId(a.getStudent().getUserId());
            dto.setStudentName(a.getStudent().getUsername());

            result.add(dto);
        }

        return result;
    }

    //助教批改打分任务
    @Override
    public void gradeTask(TaskGradingDTO dto) {
        TaskAssignment assignment = taskAssignmentRepository
                .findByTask_TaskIdAndStudent_UserId(dto.getTaskId(), dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("任务记录不存在"));

        User grader = userRepository.findById(dto.getGraderId())
                .orElseThrow(() -> new RuntimeException("批改人不存在"));

        assignment.setScore(dto.getScore());
        assignment.setGradeComment(dto.getGradeComment());
        assignment.setGrader(grader);
        assignment.setGradeTime(new Timestamp(System.currentTimeMillis()));

        taskAssignmentRepository.save(assignment);

        // ✅ 添加通知功能
        Notification notification = new Notification();
        notification.setReceiver(assignment.getStudent());
        notification.setNotificationType("任务通知");
        notification.setRefTargetId(dto.getTaskId());
        notification.setRefTargetType("任务");
        notification.setContent("您的任务已批改，分数：" + dto.getScore()
                + (dto.getGradeComment() != null ? "，评语：" + dto.getGradeComment() : ""));
        notification.setSentTime(new Timestamp(System.currentTimeMillis()));
        notification.setStatus("未读");

        notificationRepository.save(notification);
    }

    //学员任务数据统计
    @Override
    public TaskStatsDTO getStudentTaskStats(Integer studentId) {
        List<TaskAssignment> assignments = taskAssignmentRepository.findByStudent_UserId(studentId);
        long total = assignments.size();
        long completed = taskAssignmentRepository.countCompletedByStudentId(studentId);
        long incomplete = taskAssignmentRepository.countIncompleteByStudentId(studentId);

        double avgScore = assignments.stream()
                .filter(a -> a.getScore() != null)
                .mapToInt(TaskAssignment::getScore)
                .average()
                .orElse(0.0);

        TaskStatsDTO dto = new TaskStatsDTO();
        dto.setStudentId(studentId);
        dto.setTotalTasks(total);
        dto.setCompletedTasks(completed);
        dto.setIncompleteTasks(incomplete);
        dto.setAverageScore(avgScore);

        return dto;
    }

    //查看某学员某任务的提交记录
    @Override
    public StudentTaskSubmissionDTO getStudentTaskSubmission(Integer taskId, Integer studentId) {
        TaskAssignment assignment = taskAssignmentRepository
                .findByTask_TaskIdAndStudent_UserId(taskId, studentId)
                .orElseThrow(() -> new RuntimeException("未找到该学员在此任务下的提交记录"));

        StudentTaskSubmissionDTO dto = new StudentTaskSubmissionDTO();
        dto.setTaskId(assignment.getTask().getTaskId());
        dto.setStudentId(assignment.getStudent().getUserId());
        dto.setSubmitText(assignment.getSubmitText());
        dto.setSubmitTime(assignment.getSubmitTime());
        dto.setScore(assignment.getScore());
        dto.setGradeComment(assignment.getGradeComment());

        return dto;
    }
}
package com.hdu.language_learning_system.task.service;

import com.hdu.language_learning_system.task.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TaskAssignmentService {

    void submitTaskWithFile(TaskSubmissionDTO dto);

    List<TaskSubmissionDTO> getSubmissionsByStudentId(Integer studentId);

    List<TaskSubmissionDTO> getSubmissionsByTaskId(Integer taskId);

    void gradeTask(TaskGradingDTO dto);

    TaskStatsDTO getStudentTaskStats(Integer studentId);
}
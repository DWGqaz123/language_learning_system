package com.hdu.language_learning_system.task.service;

import com.hdu.language_learning_system.task.dto.*;

import java.util.List;

public interface TaskService {
    void publishTask(TaskPublishDTO dto);

    List<TaskListDTO> getTaskListByStudentId(Integer studentId);

    TaskDetailDTO getTaskDetail(Integer taskId, Integer studentId);

    List<TaskListDTO> getTasksPublishedBy(Integer publisherId);

    void updateTask(TaskUpdateDTO dto);

    void deleteTask(Integer taskId);
}

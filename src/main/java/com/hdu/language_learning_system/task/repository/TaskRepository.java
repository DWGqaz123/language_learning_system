package com.hdu.language_learning_system.task.repository;

import com.hdu.language_learning_system.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    // 查找学生相关的任务（schedule 课程作业或 student 训练任务）
    List<Task> findByStudent_UserIdOrSchedule_ScheduleIdIn(Integer studentId, List<Integer> scheduleIds);
    List<Task> findByPublisher_UserId(Integer publisherId);

    void deleteByTaskId(Integer taskId);
}

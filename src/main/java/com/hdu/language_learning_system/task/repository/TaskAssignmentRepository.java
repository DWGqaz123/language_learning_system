package com.hdu.language_learning_system.task.repository;

import com.hdu.language_learning_system.task.entity.TaskAssignment;
import com.hdu.language_learning_system.task.entity.TaskAssignmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, TaskAssignmentId> {

    // 查询某位学生所有任务分配
    List<TaskAssignment> findByStudent_UserId(Integer studentId);

    Optional<TaskAssignment> findByTask_TaskIdAndStudent_UserId(Integer taskId, Integer studentId);

    List<TaskAssignment> findByTask_TaskId(Integer taskId);

    @Query("SELECT COUNT(ta) FROM TaskAssignment ta WHERE ta.student.userId = :studentId AND ta.completionStatus = '已完成'")
    Long countCompletedByStudentId(Integer studentId);

    @Query("SELECT COUNT(ta) FROM TaskAssignment ta WHERE ta.student.userId = :studentId AND ta.completionStatus = '未完成'")
    Long countIncompleteByStudentId(Integer studentId);

}

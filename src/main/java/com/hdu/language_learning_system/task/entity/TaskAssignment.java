package com.hdu.language_learning_system.task.entity;

import com.hdu.language_learning_system.task.entity.Task;
import com.hdu.language_learning_system.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "task_assignments")
@Data
@IdClass(TaskAssignmentId.class) // 联合主键
public class TaskAssignment {

    @Id
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Id
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    private String completionStatus; // 已完成 or 未完成

    private Timestamp submitTime;

    private String attachmentPath;

    @Column(name = "submit_text", columnDefinition = "TEXT")
    private String submitText;

    @ManyToOne
    @JoinColumn(name = "grader_id")
    private User grader;

    private Timestamp gradeTime;

    private String gradeComment;

    private Integer score;
}
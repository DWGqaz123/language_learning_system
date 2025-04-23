package com.hdu.language_learning_system.task.entity;

import com.hdu.language_learning_system.course.entity.Schedule;
import com.hdu.language_learning_system.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "tasks")
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer taskId;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student; // nullable

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private User publisher;

    private String taskType;

    @Column(name = "task_content", columnDefinition = "TEXT")
    private String taskContent;

    @Column(name = "deadline")
    private Timestamp deadline;

    private Timestamp publishTime;
}

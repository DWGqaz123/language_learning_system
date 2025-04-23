package com.hdu.language_learning_system.exam.entity;

import com.hdu.language_learning_system.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "student_exam_records")
@Data
public class StudentExamRecord {

    @EmbeddedId
    private StudentExamRecordId id;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @MapsId("examId")
    @JoinColumn(name = "exam_id")
    private MockExam exam;

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "objective_score")
    private Integer objectiveScore;

    @Column(name = "subjective_score")
    private Integer subjectiveScore;

    @Column(name = "teacher_comment", columnDefinition = "TEXT")
    private String teacherComment;

    @Column(name = "assistant_comment", columnDefinition = "TEXT")
    private String assistantComment;

    @Column(name = "completed_time")
    private Timestamp completedTime;

    @Column(name = "answers_json", columnDefinition = "TEXT")
    private String answersJson; // 注意：直接作为 JSON 字符串处理

    @Column(name = "exam_status")
    private String examStatus;
}

package com.hdu.language_learning_system.analysis.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import com.hdu.language_learning_system.user.entity.User;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "student_performance_reports")
@Data
public class StudentPerformanceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reportId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    private String attendanceSummary;
    private String taskSummary;
    private String examSummary;
    private String studyRoomSummary;
    private Integer overallScore;
    private String assistantComment;

    @Column(name = "generated_time")
    private Timestamp generatedTime;
}
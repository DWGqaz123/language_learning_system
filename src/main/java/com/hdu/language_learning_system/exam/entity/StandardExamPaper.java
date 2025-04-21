package com.hdu.language_learning_system.exam.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "standard_exam_papers")
@Data
public class StandardExamPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paper_id")
    private Integer paperId;

    @Column(name = "paper_name")
    private String paperName;

    @Column(name = "exam_type")
    private String examType;

    @Column(name = "paper_content", columnDefinition = "TEXT")
    private String paperContent; // 注意：JSON 字符串

    @Column(name = "objective_answers_json", columnDefinition = "TEXT")
    private String objectiveAnswersJson;

    @Column(name = "created_time")
    private Timestamp createdTime;
}
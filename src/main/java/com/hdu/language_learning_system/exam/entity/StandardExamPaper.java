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
    private Integer paperId;

    private String paperName;

    private String examType;

    @Column(columnDefinition = "TEXT")
    private String paperContent;

    private Timestamp createdTime;
}
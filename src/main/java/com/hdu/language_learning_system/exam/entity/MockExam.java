package com.hdu.language_learning_system.exam.entity;

import com.hdu.language_learning_system.studyRoom.entity.StudyRoom;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "mock_exams")
@Data
public class MockExam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Integer examId;

    @Column(name = "exam_time")
    private Timestamp examTime;

    @Column(name = "exam_name")
    private String examName;

    @ManyToOne
    @JoinColumn(name = "standard_paper_id")
    private StandardExamPaper standardPaper;

    @ManyToOne
    @JoinColumn(name = "exam_room_id")
    private StudyRoom examRoom;
}

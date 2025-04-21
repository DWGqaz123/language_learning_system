package com.hdu.language_learning_system.exam.entity;

import com.hdu.language_learning_system.studyRoom.entity.StudyRoom;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
@Entity
@Table(name = "student_exam_records")
@Data
@IdClass(StudentExamRecordId.class)
public class StudentExamRecord {

    @Id
    private Integer studentId;

    @Id
    private Integer examId;

    private Integer totalScore;

    private Integer subjectiveScore;

    @Column(columnDefinition = "TEXT")
    private String teacherComment;

    @Column(columnDefinition = "TEXT")
    private String assistantComment;

    private Timestamp completedTime;
}

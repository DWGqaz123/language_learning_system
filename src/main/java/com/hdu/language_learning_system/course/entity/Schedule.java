package com.hdu.language_learning_system.course.entity;

import com.hdu.language_learning_system.studyRoom.entity.StudyRoom;
import com.hdu.language_learning_system.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "schedules")
@Data
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleId;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @ManyToOne
    @JoinColumn(name = "assistant_id")
    private User assistant; //

    @ManyToOne
    @JoinColumn(name = "room_id")
    private StudyRoom room;

    private Timestamp classTime;
}
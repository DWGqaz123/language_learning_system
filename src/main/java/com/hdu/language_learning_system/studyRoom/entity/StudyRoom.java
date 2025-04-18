package com.hdu.language_learning_system.studyRoom.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "study_rooms")
@Data
public class StudyRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roomId;

    private String name;
}
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

    @Column(name = "room_name")
    private String roomName;

    private Integer capacity;

    private String location;

    @Column(name = "current_status")
    private String currentStatus;
}
package com.hdu.language_learning_system.studyRoom.repository;

import com.hdu.language_learning_system.studyRoom.entity.StudyRoomReservation;
import com.hdu.language_learning_system.studyRoom.entity.StudyRoomReservationId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StudyRoomReservationRepository extends JpaRepository<StudyRoomReservation, StudyRoomReservationId> {

    List<StudyRoomReservation> findByReservationDate(LocalDate date);

    List<StudyRoomReservation> findByRoomIdAndReservationDate(Integer roomId, LocalDate date);

    List<StudyRoomReservation> findByReviewStatus(String reviewStatus);

    List<StudyRoomReservation> findByStudent_UserId(Integer studentId);
}
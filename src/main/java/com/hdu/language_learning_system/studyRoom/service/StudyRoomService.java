package com.hdu.language_learning_system.studyRoom.service;

import com.hdu.language_learning_system.studyRoom.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface StudyRoomService {
    void createStudyRoom(StudyRoomCreateDTO dto);

    List<StudyRoomListDTO> getAllRooms();

    List<StudyRoomDTO> getAvailableRoomsForDate(LocalDate date);

    void applyReservation(StudyRoomReservationRequestDTO dto);

    void reviewReservation(StudyRoomReviewDTO dto);

    List<StudyRoomReservationDTO> getPendingReservations();

    List<StudyRoomReservationDTO> getStudentReservations(Integer studentId);

    void cancelReservation(Integer roomId, Integer studentId, LocalDate reservationDate, String timeSlot);

    List<StudyRoomReservationDTO> getAllReservationsByDate(LocalDate date);

    void signStudyRoom(SignDTO dto);

    List<StudyRoomUsageStatsDTO> getStudyRoomUsageStats(Integer roomId, LocalDate date);

    StudyRoomUsageStatisticsDTO getStudentUsageStatistics(Integer studentId);

}

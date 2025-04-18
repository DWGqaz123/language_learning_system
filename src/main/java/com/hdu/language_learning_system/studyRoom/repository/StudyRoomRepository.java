package com.hdu.language_learning_system.studyRoom.repository;
import com.hdu.language_learning_system.studyRoom.entity.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRoomRepository extends JpaRepository<StudyRoom, Integer> {
}
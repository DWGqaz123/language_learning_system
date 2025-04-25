package com.hdu.language_learning_system.exam.repository;

import com.hdu.language_learning_system.exam.entity.MockExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface MockExamRepository extends JpaRepository<MockExam, Integer> {
    List<MockExam> findByExamTimeBetween(LocalDateTime start, LocalDateTime end);
    @Query("SELECT m FROM MockExam m WHERE m.examRoom.roomId = :roomId AND DATE(m.examTime) = DATE(:examTime)")
    List<MockExam> findByRoomIdAndDate(@Param("roomId") Integer roomId, @Param("examTime") Timestamp examTime);

    List<MockExam> findByExamRoom_RoomIdAndExamTimeBetween(Integer roomId, Timestamp start, Timestamp end);
}
package com.hdu.language_learning_system.exam.repository;

import com.hdu.language_learning_system.exam.entity.MockExam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MockExamRepository extends JpaRepository<MockExam, Integer> {
    List<MockExam> findByExamTimeBetween(LocalDateTime start, LocalDateTime end);
}
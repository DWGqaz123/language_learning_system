package com.hdu.language_learning_system.exam.repository;

import com.hdu.language_learning_system.exam.entity.MockExam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MockExamRepository extends JpaRepository<MockExam, Integer> {
}
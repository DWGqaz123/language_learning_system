package com.hdu.language_learning_system.exam.repository;

import com.hdu.language_learning_system.exam.entity.StandardExamPaper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StandardExamPaperRepository extends JpaRepository<StandardExamPaper, Integer> {
    List<StandardExamPaper> findByAuditStatus(String auditStatus);
}
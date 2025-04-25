package com.hdu.language_learning_system.analysis.repository;

import com.hdu.language_learning_system.analysis.dto.StudentPerformanceReportDTO;
import com.hdu.language_learning_system.analysis.entity.StudentPerformanceReport;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentPerformanceReportRepository extends JpaRepository<StudentPerformanceReport, Integer> {
    @Modifying
    @Transactional
    @Query("UPDATE StudentPerformanceReport r SET r.assistantComment = :comment WHERE r.reportId = :reportId")
    void updateAssistantComment(String comment, Integer reportId);

    List<StudentPerformanceReport> findByStudent_UserId(Integer studentId);

}
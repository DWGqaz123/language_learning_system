package com.hdu.language_learning_system.exam.repository;

import com.hdu.language_learning_system.exam.entity.StudentExamRecord;
import com.hdu.language_learning_system.exam.entity.StudentExamRecordId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentExamRecordRepository extends JpaRepository<StudentExamRecord, StudentExamRecordId> {
    List<StudentExamRecord> findByStudent_UserId(Integer studentId);
    List<StudentExamRecord> findByExam_ExamId(Integer examId);
}
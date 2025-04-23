package com.hdu.language_learning_system.exam.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class StudentExamRecordId implements Serializable {
    private Integer studentId;
    private Integer examId;
}
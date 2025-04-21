package com.hdu.language_learning_system.exam.entity;
import java.io.Serializable;
import lombok.Data;

@Data
public class StudentExamRecordId implements Serializable {
    private Integer studentId;
    private Integer examId;
}
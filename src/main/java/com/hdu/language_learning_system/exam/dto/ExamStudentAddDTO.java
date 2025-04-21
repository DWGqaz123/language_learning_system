package com.hdu.language_learning_system.exam.dto;

import lombok.Data;
import java.util.List;

@Data
public class ExamStudentAddDTO {
    private List<Integer> studentIds;
    private Integer examId;
}

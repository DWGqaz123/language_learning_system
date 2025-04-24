package com.hdu.language_learning_system.course.dto;

import lombok.Data;

import java.util.List;

@Data
public class ClassStudentOperationDTO {
    private Integer courseId;
    private List<Integer> studentIds;
}
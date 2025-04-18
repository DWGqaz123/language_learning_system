package com.hdu.language_learning_system.course.service;

import com.hdu.language_learning_system.course.dto.CourseCreateDTO;
import com.hdu.language_learning_system.course.dto.AddStudentsToCourseDTO;
public interface CourseService {
    void createCourse(CourseCreateDTO dto);

    void addStudentsToClassCourse(AddStudentsToCourseDTO dto);

    void generateStudentRecords(Integer scheduleId);

}
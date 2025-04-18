package com.hdu.language_learning_system.course.repository;

import com.hdu.language_learning_system.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Integer> {
}

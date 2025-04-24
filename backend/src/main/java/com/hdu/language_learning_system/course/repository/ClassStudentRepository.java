package com.hdu.language_learning_system.course.repository;

import com.hdu.language_learning_system.course.entity.ClassStudent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassStudentRepository extends JpaRepository<ClassStudent, Integer> {

    List<ClassStudent> findByCourse_CourseId(Integer courseId);

    boolean existsByCourse_CourseIdAndStudent_UserId(Integer courseId, Integer userId);

    List<ClassStudent> findByCourse_CourseIdAndStudent_UserIdIn(Integer courseId, List<Integer> userIds);

    List<ClassStudent> findByStudent_UserId(Integer userId);

    void deleteByCourse_CourseId(Integer courseId);

}
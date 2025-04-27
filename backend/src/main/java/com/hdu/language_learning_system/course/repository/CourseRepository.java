package com.hdu.language_learning_system.course.repository;

import com.hdu.language_learning_system.course.entity.Course;
import com.hdu.language_learning_system.course.entity.StudentScheduleRecord;
import com.hdu.language_learning_system.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    void deleteById(Integer courseId);


    // 查询该学员关联的所有课程（1对1 或 班级课中注册的）
    List<Course> findByStudent(User student);

    @Query("SELECT DISTINCT cs.student FROM ClassStudent cs WHERE cs.course.courseId = :courseId")
    List<User> findClassStudentsByCourseId(@Param("courseId") Integer courseId);

    // 查询某学员作为1对1绑定学生的课程
    List<Course> findByStudent_UserId(Integer userId);

}

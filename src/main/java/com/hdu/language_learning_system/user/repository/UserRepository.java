package com.hdu.language_learning_system.user.repository;

import com.hdu.language_learning_system.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    // 可添加额外查询方法，例如通过手机号查找用户
    User findByPhoneNumber(String phoneNumber);
    @Query("SELECT DISTINCT r.student FROM StudentScheduleRecord r WHERE r.course.courseId = :courseId")
    List<User> findStudentsByCourseId(@Param("courseId") Integer courseId);
}

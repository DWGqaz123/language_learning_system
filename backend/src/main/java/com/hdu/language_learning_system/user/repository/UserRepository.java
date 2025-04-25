package com.hdu.language_learning_system.user.repository;

import com.hdu.language_learning_system.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    // 可添加额外查询方法，例如通过手机号查找用户
    User findByPhoneNumber(String phoneNumber);
    @Query("SELECT DISTINCT r.student FROM StudentScheduleRecord r WHERE r.course.courseId = :courseId")
    List<User> findStudentsByCourseId(@Param("courseId") Integer courseId);

    List<User> findByPendingUpdateJsonIsNotNull();

    @Query("SELECT u FROM User u WHERE u.role.roleId = 1")
    List<User> findAllStudents();
}

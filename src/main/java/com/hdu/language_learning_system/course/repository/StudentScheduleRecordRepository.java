package com.hdu.language_learning_system.course.repository;

import com.hdu.language_learning_system.course.entity.StudentScheduleRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hdu.language_learning_system.user.entity.User;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentScheduleRecordRepository extends JpaRepository<StudentScheduleRecord, Integer> {

    // 查询 schedule_id 为 null 且 student 属于该 course 的记录
    @Query("SELECT r FROM StudentScheduleRecord r " +
            "WHERE r.schedule IS NULL " +
            "AND r.student.userId IN (" +
            "   SELECT c.student.userId FROM Course c WHERE c.courseId = :courseId)")
    List<StudentScheduleRecord> findAllByScheduleIsNullAndCourse_CourseId(Integer courseId);

    @Query("SELECT ssr.student FROM StudentScheduleRecord ssr WHERE ssr.schedule.scheduleId = :scheduleId")
    List<User> findStudentsByScheduleId(@Param("scheduleId") Integer scheduleId);

}
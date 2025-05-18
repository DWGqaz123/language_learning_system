package com.hdu.language_learning_system.course.repository;

import com.hdu.language_learning_system.course.entity.StudentScheduleRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.hdu.language_learning_system.user.entity.User;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface StudentScheduleRecordRepository extends JpaRepository<StudentScheduleRecord, Integer> {

    @Query("SELECT ssr FROM StudentScheduleRecord ssr WHERE ssr.student.userId = :studentId AND ssr.schedule.scheduleId = :scheduleId")
    Optional<StudentScheduleRecord> findByStudentIdAndScheduleId(@Param("studentId") Integer studentId,
                                                                 @Param("scheduleId") Integer scheduleId);

    @Query("SELECT COUNT(ssr) FROM StudentScheduleRecord ssr WHERE ssr.course.courseId = :courseId")
    Long countTotalByCourseId(@Param("courseId") Integer courseId);

    @Query("SELECT COUNT(ssr) FROM StudentScheduleRecord ssr WHERE ssr.course.courseId = :courseId AND ssr.attendStatus = '出勤'")
    Long countAttendByCourseId(@Param("courseId") Integer courseId);

    @Query("SELECT COUNT(ssr) FROM StudentScheduleRecord ssr WHERE ssr.course.courseId = :courseId AND ssr.attendStatus = '缺勤'")
    Long countAbsentByCourseId(@Param("courseId") Integer courseId);

    @Query("SELECT COUNT(ssr) FROM StudentScheduleRecord ssr WHERE ssr.course.courseId = :courseId AND ssr.attendStatus = '请假'")
    Long countLeaveByCourseId(@Param("courseId") Integer courseId);

    @Query("SELECT ssr FROM StudentScheduleRecord ssr WHERE ssr.schedule.scheduleId = :scheduleId AND ssr.student.userId = :studentId")
    Optional<StudentScheduleRecord> findByScheduleIdAndStudentId(@Param("scheduleId") Integer scheduleId, @Param("studentId") Integer studentId);

    Optional<StudentScheduleRecord> findBySchedule_ScheduleIdAndStudent_UserId(Integer scheduleId, Integer studentId);

    @Query("SELECT r FROM StudentScheduleRecord r WHERE r.schedule.scheduleId = :scheduleId")
    List<StudentScheduleRecord> findByScheduleId(@Param("scheduleId") Integer scheduleId);

    @Query("SELECT r FROM StudentScheduleRecord r WHERE r.student.userId = :studentId")
    List<StudentScheduleRecord> findByStudentId(@Param("studentId") Integer studentId);

    List<StudentScheduleRecord> findByStudent_UserId(Integer userId);

    @Query("SELECT ssr.student FROM StudentScheduleRecord ssr WHERE ssr.schedule.scheduleId = :scheduleId")
    List<User> findStudentsByScheduleId(@Param("scheduleId") Integer scheduleId);


    List<StudentScheduleRecord> findBySchedule_ScheduleId(Integer scheduleId);

    List<StudentScheduleRecord> findByStudent(User student);


    void deleteBySchedule_ScheduleId(Integer scheduleId);

    List<StudentScheduleRecord> findBySchedule_ScheduleIdAndAttendStatus(Integer scheduleId, String attendStatus);


    @Query("SELECT r FROM StudentScheduleRecord r " +
            "WHERE r.schedule.teacher.userId = :teacherId " +
            "AND r.PerformanceEval IS NULL")
    List<StudentScheduleRecord> findUnEvaluatedRecordsByTeacherId(Integer teacherId);

    Optional<StudentScheduleRecord> findByStudent_UserIdAndSchedule_ScheduleId(Integer studentId, Integer scheduleId);

    @Query("SELECT AVG(s.teacherFeedbackScore) FROM StudentScheduleRecord s WHERE s.schedule.scheduleId = :scheduleId AND s.teacherFeedbackScore IS NOT NULL")
    Double findAverageTeacherFeedbackByScheduleId(@Param("scheduleId") Integer scheduleId);
}
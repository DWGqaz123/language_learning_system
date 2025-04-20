package com.hdu.language_learning_system.course.repository;

import com.hdu.language_learning_system.course.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {


    @EntityGraph(attributePaths = {"room", "teacher", "assistant"})
    List<Schedule> findByCourse_CourseId(Integer courseId);

    List<Schedule> findByTeacher_UserId(Integer teacherId);

    List<Schedule> findByAssistant_UserId(Integer assistantId);

    @Query("SELECT s FROM Schedule s JOIN StudentScheduleRecord ssr ON s.scheduleId = ssr.schedule.scheduleId WHERE ssr.student.userId = :studentId")
    List<Schedule> findSchedulesByStudentId(Integer studentId);


}
package com.hdu.language_learning_system.course.repository;

import com.hdu.language_learning_system.course.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
}
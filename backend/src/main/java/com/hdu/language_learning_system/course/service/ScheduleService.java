package com.hdu.language_learning_system.course.service;

import com.hdu.language_learning_system.course.dto.ScheduleCreateDTO;
import com.hdu.language_learning_system.course.dto.ScheduleUpdateDTO;
public interface ScheduleService {
    void createSchedule(ScheduleCreateDTO dto);

    void updateSchedule(ScheduleUpdateDTO dto);

    void deleteSchedule(Integer scheduleId);

}

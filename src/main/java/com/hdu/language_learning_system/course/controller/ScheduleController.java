package com.hdu.language_learning_system.course.controller;

import com.hdu.language_learning_system.common.ApiResponse;
import com.hdu.language_learning_system.course.dto.ScheduleCreateDTO;
import com.hdu.language_learning_system.course.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    // 创建课程表接口
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createSchedule(@RequestBody ScheduleCreateDTO dto) {
        scheduleService.createSchedule(dto);
        return ResponseEntity.ok(ApiResponse.success("课程安排创建成功", null));
    }
}
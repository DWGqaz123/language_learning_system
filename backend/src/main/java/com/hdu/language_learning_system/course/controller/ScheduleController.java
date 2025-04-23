package com.hdu.language_learning_system.course.controller;

import com.hdu.language_learning_system.common.ApiResponse;
import com.hdu.language_learning_system.course.dto.ScheduleCreateDTO;
import com.hdu.language_learning_system.course.dto.ScheduleUpdateDTO;
import com.hdu.language_learning_system.course.service.CourseService;
import com.hdu.language_learning_system.course.service.ScheduleService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Resource
    private CourseService courseService;

    // 创建课程表接口
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createSchedule(@RequestBody ScheduleCreateDTO dto) {
        scheduleService.createSchedule(dto);
        return ResponseEntity.ok(ApiResponse.success("课程安排创建成功", null));
    }
    //修改排课
    @PutMapping("/update")
    public ApiResponse<String> updateSchedule(@RequestBody ScheduleUpdateDTO dto) {
        scheduleService.updateSchedule(dto);
        return ApiResponse.success("排课信息更新成功");
    }

    //删除排课
    @DeleteMapping("/{scheduleId}/delete")
    public ApiResponse<String> deleteSchedule(@PathVariable Integer scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ApiResponse.success("排课删除成功");
    }
}
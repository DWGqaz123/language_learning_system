package com.hdu.language_learning_system.task.controller;

import com.hdu.language_learning_system.common.ApiResponse;
import com.hdu.language_learning_system.task.dto.*;
import com.hdu.language_learning_system.task.entity.*;
import java.util.List;

import com.hdu.language_learning_system.task.service.TaskAssignmentService;
import com.hdu.language_learning_system.task.service.TaskService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Resource
    private TaskService taskService;
    @Resource
    private TaskAssignmentService taskAssignmentService;

    //课后任务发布
    @PostMapping("/publish")
    public ApiResponse<String> publishTask(@RequestBody TaskPublishDTO dto) {
        taskService.publishTask(dto);
        return ApiResponse.success("任务发布成功");
    }

    //学员查看课后任务清单
    @GetMapping("/student/{studentId}/list")
    public ApiResponse<List<TaskListDTO>> getTaskListByStudentId(@PathVariable Integer studentId) {
        List<TaskListDTO> list = taskService.getTaskListByStudentId(studentId);
        return ApiResponse.success(list);
    }


    //查看任务详情
    @GetMapping("/{taskId}/details")
    public ApiResponse<TaskDetailDTO> getTaskDetail(
            @PathVariable Integer taskId,
            @RequestParam(value = "studentId", required = false) Integer studentId
    ) {
        TaskDetailDTO detail = taskService.getTaskDetail(taskId, studentId);
        return ApiResponse.success(detail);
    }
    //助教查看自己发布的任务
    @GetMapping("/published")
    public ApiResponse<List<TaskListDTO>> getMyPublishedTasks(@RequestParam Integer publisherId) {
        List<TaskListDTO> list = taskService.getTasksPublishedBy(publisherId);
        return ApiResponse.success(list);
    }

    //助教修改已发布的任务
    @PutMapping("/update")
    public ApiResponse<String> updateTask(@RequestBody TaskUpdateDTO dto) {
        taskService.updateTask(dto);
        return ApiResponse.success("任务更新成功");
    }

    //助教删除已发布的任务
    @DeleteMapping("/{taskId}")
    public ApiResponse<String> deleteTask(@PathVariable Integer taskId) {
        taskService.deleteTask(taskId);
        return ApiResponse.success("任务删除成功");
    }

}

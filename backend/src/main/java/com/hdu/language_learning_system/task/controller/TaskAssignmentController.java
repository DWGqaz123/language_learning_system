package com.hdu.language_learning_system.task.controller;

import com.hdu.language_learning_system.common.ApiResponse;
import com.hdu.language_learning_system.task.dto.*;
import com.hdu.language_learning_system.task.service.TaskAssignmentService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/task-assignments")
public class TaskAssignmentController {

    @Resource
    private TaskAssignmentService taskAssignmentService;

    //学员提交课后任务
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> submitTaskMultipart(
            @RequestParam("taskId") Integer taskId,
            @RequestParam("studentId") Integer studentId,
            @RequestParam(value = "submitText", required = false) String submitText,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        TaskSubmissionDTO dto = new TaskSubmissionDTO();
        dto.setTaskId(taskId);
        dto.setStudentId(studentId);
        dto.setSubmitText(submitText);
        dto.setAttachmentFile(file); // 控制层只做参数封装

        try {
            taskAssignmentService.submitTaskWithFile(dto);
            return ApiResponse.success("提交成功");
        } catch (Exception e) {
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }

    // 助教查看某个学员的任务提交记录
    @GetMapping("/student/{studentId}/submissions")
    public ApiResponse<List<TaskSubmissionDTO>> getSubmissions(@PathVariable Integer studentId) {
        List<TaskSubmissionDTO> list = taskAssignmentService.getSubmissionsByStudentId(studentId);
        return ApiResponse.success(list);
    }
    //助教查看某个任务的所有提交记录
    @GetMapping("/task/{taskId}/submissions")
    public ApiResponse<List<TaskSubmissionDTO>> getSubmissionsByTaskId(@PathVariable Integer taskId) {
        List<TaskSubmissionDTO> list = taskAssignmentService.getSubmissionsByTaskId(taskId);
        return ApiResponse.success(list);
    }
    //助教批改打分任务
    @PostMapping("/grade")
    public ApiResponse<String> gradeTask(@RequestBody TaskGradingDTO dto) {
        taskAssignmentService.gradeTask(dto);
        return ApiResponse.success("批改成功");
    }

    @GetMapping("/student/{studentId}/stats")
    public ApiResponse<TaskStatsDTO> getStudentTaskStats(@PathVariable Integer studentId) {
        TaskStatsDTO stats = taskAssignmentService.getStudentTaskStats(studentId);
        return ApiResponse.success(stats);
    }
}
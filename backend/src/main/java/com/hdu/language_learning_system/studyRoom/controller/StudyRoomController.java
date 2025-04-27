package com.hdu.language_learning_system.studyRoom.controller;

import com.hdu.language_learning_system.common.ApiResponse;
import com.hdu.language_learning_system.studyRoom.dto.*;
import com.hdu.language_learning_system.studyRoom.service.StudyRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/study-rooms")
public class StudyRoomController {

    @Autowired
    private StudyRoomService studyRoomService;

    // 管理员录入自习室
    @PostMapping
    public ApiResponse<String> createStudyRoom(@RequestBody StudyRoomCreateDTO dto) {
        try {
            studyRoomService.createStudyRoom(dto);
            return ApiResponse.success("自习室录入成功");
        } catch (Exception e) {
            return ApiResponse.error("录入失败：" + e.getMessage());
        }
    }

    //查看自习室列表
    @GetMapping("/all")
    public ApiResponse<List<StudyRoomListDTO>> getAllStudyRooms() {
        try {
            List<StudyRoomListDTO> list = studyRoomService.getAllRooms();
            return ApiResponse.success(list);
        } catch (Exception e) {
            return ApiResponse.error("查询自习室列表失败：" + e.getMessage());
        }
    }

    // 获取自习室预约信息（默认展示明天）
    @GetMapping("/reservations")
    public ApiResponse<List<StudyRoomDTO>> getReservationList(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        if (date == null) {
            date = LocalDate.now().plusDays(1); // 默认明天
        }

        return ApiResponse.success(studyRoomService.getAvailableRoomsForDate(date));
    }

    // 学员提交预约申请
    @PostMapping("/reserve")
    public ApiResponse<String> applyReservation(@RequestBody StudyRoomReservationRequestDTO dto) {
        studyRoomService.applyReservation(dto);
        return ApiResponse.success("预约申请已提交");
    }

    // 管理员审核自习室预约
    @PutMapping("/review")
    public ApiResponse<String> reviewReservation(@RequestBody StudyRoomReviewDTO dto) {
        try {
            studyRoomService.reviewReservation(dto);
            return ApiResponse.success("审核成功");
        } catch (Exception e) {
            return ApiResponse.error("审核失败：" + e.getMessage());
        }
    }

    // 查看待审核预约申请列表
    @GetMapping("/reservations/pending")
    public ApiResponse<List<StudyRoomReservationDTO>> getPendingReservations() {
        return ApiResponse.success(studyRoomService.getPendingReservations());
    }

    //学员查看自己的预约记录
    @GetMapping("/my-reservations")
    public ApiResponse<List<StudyRoomReservationDTO>> getStudentReservations(@RequestParam Integer studentId) {
        return ApiResponse.success(studyRoomService.getStudentReservations(studentId));
    }

    //学员删除自己的预约申请
    @DeleteMapping("/cancel-reservation")
    public ApiResponse<String> cancelReservation(
            @RequestParam Integer roomId,
            @RequestParam Integer studentId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate reservationDate,
            @RequestParam String timeSlot) {
        try {
            studyRoomService.cancelReservation(roomId, studentId, reservationDate, timeSlot);
            return ApiResponse.success("取消成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error("取消失败：" + e.getMessage());
        }
    }

    //管理员查看所有预约记录
    @GetMapping("/reservations/all")
    public ApiResponse<List<StudyRoomReservationDTO>> getAllReservationsByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            List<StudyRoomReservationDTO> list = studyRoomService.getAllReservationsByDate(date);
            return ApiResponse.success(list);
        } catch (Exception e) {
            return ApiResponse.error("获取预约记录失败：" + e.getMessage());
        }
    }

    //学员签到自习室
    @PostMapping("/sign")
    public ApiResponse<String> signStudyRoom(@RequestBody SignDTO dto) {
        try {
            studyRoomService.signStudyRoom(dto);
            return ApiResponse.success("操作成功");
        } catch (Exception e) {
            return ApiResponse.error("操作失败：" + e.getMessage());
        }
    }

    //自习室使用统计
    @GetMapping("/usage-stats")
    public ApiResponse<List<StudyRoomUsageStatsDTO>> getUsageStats(
            @RequestParam Integer roomId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            List<StudyRoomUsageStatsDTO> stats = studyRoomService.getStudyRoomUsageStats(roomId, date);
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error("获取使用统计失败：" + e.getMessage());
        }
    }

    //学员自习室使用统计
    @GetMapping("/usage-statistics/{studentId}")
    public ApiResponse<StudyRoomUsageStatisticsDTO> getStudentUsageStatistics(@PathVariable Integer studentId) {
        try {
            return ApiResponse.success(studyRoomService.getStudentUsageStatistics(studentId));
        } catch (Exception e) {
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }

    // 更新自习室
    @PutMapping("/update")
    public ApiResponse<Void> updateStudyRoom(@RequestBody StudyRoomDTO dto) {
        studyRoomService.updateStudyRoom(dto);
        return ApiResponse.success("自习室更新成功", null);
    }

    // 删除自习室
    @DeleteMapping("/delete/{roomId}")
    public ApiResponse<Void> deleteStudyRoom(@PathVariable Integer roomId) {
        studyRoomService.deleteStudyRoom(roomId);
        return ApiResponse.success("自习室删除成功", null);
    }
}

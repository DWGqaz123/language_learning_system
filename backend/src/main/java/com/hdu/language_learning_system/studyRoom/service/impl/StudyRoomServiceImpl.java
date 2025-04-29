package com.hdu.language_learning_system.studyRoom.service.impl;

import com.hdu.language_learning_system.course.repository.ScheduleRepository;
import com.hdu.language_learning_system.exam.repository.MockExamRepository;
import com.hdu.language_learning_system.studyRoom.dto.*;
import com.hdu.language_learning_system.studyRoom.entity.StudyRoom;
import com.hdu.language_learning_system.studyRoom.entity.StudyRoomReservation;
import com.hdu.language_learning_system.studyRoom.entity.StudyRoomReservationId;
import com.hdu.language_learning_system.studyRoom.repository.StudyRoomRepository;
import com.hdu.language_learning_system.studyRoom.repository.StudyRoomReservationRepository;
import com.hdu.language_learning_system.studyRoom.service.StudyRoomService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
public class StudyRoomServiceImpl implements StudyRoomService {

    @Autowired
    private StudyRoomRepository studyRoomRepository;

    @Autowired
    private StudyRoomReservationRepository studyRoomReservationRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MockExamRepository mockExamRepository;

    private static final List<String> TIME_SLOTS = Arrays.asList("上午", "下午", "晚上");
    // 录入新自习室
    @Override
    public void createStudyRoom(StudyRoomCreateDTO dto) {
        if (studyRoomRepository.existsByRoomName(dto.getRoomName())) {
            throw new RuntimeException("该自习室名称已存在");
        }

        StudyRoom room = new StudyRoom();
        room.setRoomName(dto.getRoomName());
        room.setCapacity(dto.getCapacity());
        room.setLocation(dto.getLocation());
        room.setCurrentStatus("空闲");

        studyRoomRepository.save(room);
    }
    //查看自习室列表
    @Override
    public List<StudyRoomListDTO> getAllRooms() {
        return studyRoomRepository.findAll()
                .stream()
                .map(room -> {
                    StudyRoomListDTO dto = new StudyRoomListDTO();
                    dto.setRoomId(room.getRoomId());
                    dto.setRoomName(room.getRoomName());
                    dto.setCapacity(room.getCapacity());
                    dto.setLocation(room.getLocation());
                    dto.setCurrentStatus(room.getCurrentStatus());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 获取当天自习室状态
    @Override
    public List<StudyRoomDTO> getAvailableRoomsForDate(LocalDate date) {
        List<StudyRoom> rooms = studyRoomRepository.findAll();
        List<StudyRoomDTO> result = new ArrayList<>();

        // 查找该日所有预约记录
        List<StudyRoomReservation> reservations = studyRoomReservationRepository.findByReservationDate(date);
        Map<String, String> reservedMap = new HashMap<>();
        for (StudyRoomReservation r : reservations) {
            reservedMap.put(r.getRoomId() + "_" + r.getTimeSlot(), r.getReviewStatus());
        }

        // 查询是否有课程或考试占用
        Set<Integer> unavailableRooms = new HashSet<>();
        scheduleRepository.findByClassTimeBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay())
                .forEach(s -> unavailableRooms.add(s.getRoom().getRoomId()));
        mockExamRepository.findByExamTimeBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay())
                .forEach(e -> unavailableRooms.add(e.getExamRoom().getRoomId()));

        for (StudyRoom room : rooms) {
            // 过滤掉容量不为 1 的教室
            if (room.getCapacity() != 1) {
                continue;
            }

            StudyRoomDTO dto = new StudyRoomDTO();
            dto.setRoomId(room.getRoomId());
            dto.setRoomName(room.getRoomName());
            dto.setCapacity(room.getCapacity());
            dto.setLocation(room.getLocation());

            Map<String, String> slotStatus = new HashMap<>();
            for (String slot : TIME_SLOTS) {
                if (unavailableRooms.contains(room.getRoomId())) {
                    slotStatus.put(slot, "不可预约");
                } else {
                    String key = room.getRoomId() + "_" + slot;
                    String status = reservedMap.getOrDefault(key, "可预约");

                    switch (status) {
                        case "通过" -> status = "已预约";
                        case "待审核" -> status = "待审核";
                        case "驳回" -> status = "可预约";
                        default -> status = "可预约";
                    }

                    slotStatus.put(slot, status);
                }
            }

            dto.setTimeSlotStatus(slotStatus);
            result.add(dto);
        }

        return result;
    }

    //学员申请预约自习室
    @Override
    public void applyReservation(StudyRoomReservationRequestDTO dto) {
        StudyRoomReservationId id = new StudyRoomReservationId(
                dto.getRoomId(), dto.getStudentId(), dto.getReservationDate(), dto.getTimeSlot());

        if (studyRoomReservationRepository.existsById(id)) {
            throw new RuntimeException("已提交预约，不可重复提交");
        }

        StudyRoomReservation r = new StudyRoomReservation();
        r.setRoomId(dto.getRoomId());
        r.setStudentId(dto.getStudentId());
        r.setReservationDate(dto.getReservationDate());
        r.setTimeSlot(dto.getTimeSlot());
        r.setReviewStatus("待审核");

        studyRoomReservationRepository.save(r);
    }

    //审核预约申请
    @Override
    public void reviewReservation(StudyRoomReviewDTO dto) {
        StudyRoomReservationId id = new StudyRoomReservationId(
                dto.getRoomId(), dto.getStudentId(), dto.getReservationDate(), dto.getTimeSlot());

        StudyRoomReservation record = studyRoomReservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("未找到预约记录"));

        record.setReviewStatus(dto.getReviewStatus());
        studyRoomReservationRepository.save(record);
    }

    // 查看待审核预约申请列表
    @Override
    public List<StudyRoomReservationDTO> getPendingReservations() {
        List<StudyRoomReservation> reservations = studyRoomReservationRepository.findByReviewStatus("待审核");
        return reservations.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private StudyRoomReservationDTO toDTO(StudyRoomReservation reservation) {
        StudyRoomReservationDTO dto = new StudyRoomReservationDTO();
        dto.setRoomId(reservation.getRoomId());
        dto.setStudentId(reservation.getStudentId());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setTimeSlot(reservation.getTimeSlot());
        dto.setReviewStatus(reservation.getReviewStatus());
        dto.setRoomName(reservation.getRoom().getRoomName());
        dto.setStudentName(reservation.getStudent().getUsername());

        // ✨ 新增：补充自习室详细信息
        if (reservation.getRoom() != null) {
            dto.setRoomName(reservation.getRoom().getRoomName());
            dto.setLocation(reservation.getRoom().getLocation());
            dto.setCapacity(reservation.getRoom().getCapacity());
        }

        return dto;
    }

    //学员查看自己的预约记录
    @Override
    public List<StudyRoomReservationDTO> getStudentReservations(Integer studentId) {
        List<StudyRoomReservation> reservations = studyRoomReservationRepository.findByStudent_UserId(studentId);
        List<StudyRoomReservationDTO> result = new ArrayList<>();

        for (StudyRoomReservation r : reservations) {
            StudyRoomReservationDTO dto = new StudyRoomReservationDTO();
            dto.setRoomId(r.getRoomId());
            dto.setRoomName(r.getRoom().getRoomName());
            dto.setLocation(r.getRoom().getLocation());
            dto.setReservationDate(r.getReservationDate());
            dto.setTimeSlot(r.getTimeSlot());
            dto.setReviewStatus(r.getReviewStatus());
            dto.setSignInTime(r.getSignInTime());
            dto.setSignOutTime(r.getSignOutTime());
            result.add(dto);
        }

        return result;
    }

    //学员删除预约申请（仅待审核）
    @Override
    public void cancelReservation(Integer roomId, Integer studentId, LocalDate reservationDate, String timeSlot) {
        StudyRoomReservationId id = new StudyRoomReservationId(roomId, studentId, reservationDate, timeSlot);
        StudyRoomReservation reservation = studyRoomReservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预约记录不存在"));

        if (!"待审核".equals(reservation.getReviewStatus())) {
            throw new RuntimeException("仅待审核的预约申请可以取消");
        }

        studyRoomReservationRepository.deleteById(id);
    }

    //管理员查看所有预约记录
    @Override
    public List<StudyRoomReservationDTO> getAllReservationsByDate(LocalDate date) {
        List<StudyRoomReservation> reservations = studyRoomReservationRepository.findByReservationDate(date);
        return reservations.stream().map(r -> {
            StudyRoomReservationDTO dto = new StudyRoomReservationDTO();
            dto.setRoomId(r.getRoomId());
            dto.setRoomName(r.getRoom().getRoomName());
            dto.setLocation(r.getRoom().getLocation());
            dto.setCapacity(r.getRoom().getCapacity());

            dto.setStudentId(r.getStudentId());
            dto.setStudentName(r.getStudent().getUsername());

            dto.setReservationDate(r.getReservationDate());
            dto.setTimeSlot(r.getTimeSlot());
            dto.setReviewStatus(r.getReviewStatus());
            return dto;
        }).toList();
    }

    // 签到签退自习室
    @Override
    @Transactional
    public void signStudyRoom(SignDTO dto) {
        StudyRoomReservationId id = new StudyRoomReservationId(
                dto.getRoomId(),
                dto.getStudentId(),
                dto.getReservationDate(),
                dto.getTimeSlot()
        );

        StudyRoomReservation reservation = studyRoomReservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预约记录不存在"));

        if (!"通过".equals(reservation.getReviewStatus())) {
            throw new RuntimeException("该预约尚未审核通过，无法签到/签退");
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());

        if ("signIn".equals(dto.getAction())) {
            if (reservation.getSignInTime() != null) {
                throw new RuntimeException("已签到，不能重复签到");
            }
            reservation.setSignInTime(now);
            studyRoomReservationRepository.saveAndFlush(reservation); // ⭐️ 强制立即保存
        } else if ("signOut".equals(dto.getAction())) {
            if (reservation.getSignOutTime() != null) {
                throw new RuntimeException("已签退，不能重复签退");
            }
            reservation.setSignOutTime(now);
            studyRoomReservationRepository.saveAndFlush(reservation); // ⭐️ 强制立即保存
        } else {
            throw new RuntimeException("非法操作类型");
        }

        // 签到和签退都完成后，更新预约状态为“已签退”
        if (reservation.getSignInTime() != null && reservation.getSignOutTime() != null) {
            reservation.setReviewStatus("已签退");
            studyRoomReservationRepository.saveAndFlush(reservation); // ⭐️ 再次强制保存
        }
    }

    //自习室使用统计

    @Override
    public List<StudyRoomUsageStatsDTO> getStudyRoomUsageStats(Integer roomId, LocalDate date) {
        List<StudyRoomReservation> reservations = studyRoomReservationRepository
                .findByRoomIdAndReservationDate(roomId, date);

        Map<String, StudyRoomUsageStatsDTO> statsMap = new HashMap<>();
        for (String slot : List.of("上午", "下午", "晚上")) {
            StudyRoomUsageStatsDTO dto = new StudyRoomUsageStatsDTO();
            dto.setTimeSlot(slot);
            dto.setTotalReservations(0);
            dto.setSignedInCount(0);
            dto.setSignedOutCount(0);
            statsMap.put(slot, dto);
        }

        for (StudyRoomReservation r : reservations) {
            String slot = r.getTimeSlot();
            StudyRoomUsageStatsDTO stat = statsMap.get(slot);
            if (stat != null) {
                stat.setTotalReservations(stat.getTotalReservations() + 1);
                if (r.getSignInTime() != null) {
                    stat.setSignedInCount(stat.getSignedInCount() + 1);
                }
                if (r.getSignOutTime() != null) {
                    stat.setSignedOutCount(stat.getSignedOutCount() + 1);
                }
            }
        }

        return new ArrayList<>(statsMap.values());
    }

    //学员自习室统计
    @Override
    public StudyRoomUsageStatisticsDTO getStudentUsageStatistics(Integer studentId) {
        List<StudyRoomReservation> records = studyRoomReservationRepository.findByStudent_UserId(studentId);

        int total = 0;
        int morning = 0, afternoon = 0, evening = 0;

        for (StudyRoomReservation record : records) {
            if (!"已签退".equals(record.getReviewStatus())) continue;
            if (record.getSignInTime() == null && record.getSignOutTime() == null) continue;

            total++;
            switch (record.getTimeSlot()) {
                case "上午" -> morning++;
                case "下午" -> afternoon++;
                case "晚上" -> evening++;
            }
        }

        StudyRoomUsageStatisticsDTO dto = new StudyRoomUsageStatisticsDTO();
        dto.setTotalUsageCount(total);
        dto.setMorningCount(morning);
        dto.setAfternoonCount(afternoon);
        dto.setEveningCount(evening);

        return dto;
    }
    // 更新自习室信息
    @Override
    @Transactional
    public void updateStudyRoom(StudyRoomDTO dto) {
        StudyRoom room = studyRoomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("自习室不存在"));

        room.setRoomName(dto.getRoomName());
        room.setCapacity(dto.getCapacity());
        room.setLocation(dto.getLocation());

        studyRoomRepository.save(room);
    }

    // 删除自习室
    @Override
    @Transactional
    public void deleteStudyRoom(Integer roomId) {
        StudyRoom room = studyRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("自习室不存在"));

        studyRoomRepository.delete(room);
    }

}
package com.hdu.language_learning_system.studyRoom.entity;

import com.hdu.language_learning_system.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "study_room_reservations")
@IdClass(StudyRoomReservationId.class)
@Data
public class StudyRoomReservation {

    // 复合主键字段
    @Id
    @Column(name = "room_id")
    private Integer roomId;

    @Id
    @Column(name = "student_id")
    private Integer studentId;

    @Id
    @Column(name = "reservation_date")
    private LocalDate reservationDate;

    @Id
    @Column(name = "time_slot")
    private String timeSlot; // 上午 / 下午 / 晚上

    @Column(name = "review_status")
    private String reviewStatus; // 待审核 / 通过 / 驳回

    // 外键关联，设置为只读字段（由主键字段控制）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    private StudyRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private User student;

    @Column(name = "sign_in_time")
    private Timestamp signInTime;

    @Column(name = "sign_out_time")
    private Timestamp signOutTime;
}
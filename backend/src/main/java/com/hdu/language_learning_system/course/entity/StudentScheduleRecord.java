package com.hdu.language_learning_system.course.entity;
import com.hdu.language_learning_system.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "student_schedule_records")
@Data // Lombok
public class StudentScheduleRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ssrId;

    @ManyToOne
    @JoinColumn(name = "schedule_id") // <- 关键注解
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private Timestamp joinTime;

    @Column(name = "attend_status")
    private String attendStatus;

    @Column(name = "leave_reason")
    private String leaveReason;

    @Column(name = "performance_eval")
    private String PerformanceEval;

    @Column(name = "review_comment")
    private String reviewComment;  // 审核意见，由助教填写

    @Column(name = "teacher_feedback_score")
    private Integer teacherFeedbackScore; // 学员对教师的综合评价（如：1~5 分）

}

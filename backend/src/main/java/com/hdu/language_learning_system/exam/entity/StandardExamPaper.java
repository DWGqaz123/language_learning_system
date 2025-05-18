package com.hdu.language_learning_system.exam.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.hdu.language_learning_system.user.entity.User;
import java.sql.Timestamp;

@Entity
@Table(name = "standard_exam_papers")
@Data
public class StandardExamPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paper_id")
    private Integer paperId;

    @Column(name = "paper_name")
    private String paperName;

    @Column(name = "exam_type")
    private String examType;

    @Column(name = "paper_content", columnDefinition = "TEXT")
    private String paperContent; // 注意：JSON 字符串

    @Column(name = "objective_answers_json", columnDefinition = "TEXT")
    private String objectiveAnswersJson;

    @Column(name = "created_time")
    private Timestamp createdTime;

    @Column(name = "audit_status")
    private String auditStatus; // 待审核、已通过、已驳回

    @Column(name = "audit_comment")
    private String auditComment;

    @ManyToOne
    @JoinColumn(name = "audited_by")
    private User auditedBy;

}
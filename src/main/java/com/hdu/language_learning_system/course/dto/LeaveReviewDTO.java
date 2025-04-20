package com.hdu.language_learning_system.course.dto;

import lombok.Data;

@Data
public class LeaveReviewDTO {
    private Integer ssrId;             // 学员课程记录 ID
    private Boolean approved;          // 是否通过审核
    private String reviewComment;      // 审核意见
}
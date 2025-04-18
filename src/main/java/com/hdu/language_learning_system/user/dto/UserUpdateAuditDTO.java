package com.hdu.language_learning_system.user.dto;

import lombok.Data;

@Data
public class UserUpdateAuditDTO {
    private Integer userId;
    private String username;
    private String phoneNumber;
    private String description;
    private Boolean approved; // 审核是否通过
}
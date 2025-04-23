package com.hdu.language_learning_system.user.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private Integer userId;
    private String username;
    private String phoneNumber;
    private String roleName;
    private Boolean accountStatus;
    // 可拓展字段：token、头像、登录时间等
}
package com.hdu.language_learning_system.user.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserDTO {
    private Integer userId;
    private String username;
    private String phoneNumber;
    private String roleName; // 角色名称
    private Boolean accountStatus;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer roleId;
    private Integer lessonHours;
    private String password;
}

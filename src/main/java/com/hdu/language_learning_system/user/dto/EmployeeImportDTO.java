package com.hdu.language_learning_system.user.dto;

import lombok.Data;

@Data
public class EmployeeImportDTO {
    private String username;
    private String phoneNumber;
    private String password;
    private Integer roleId;  // 教师=2，助教=3
    private Boolean accountStatus;  // 表示是否启用账户
    private String description;  // 如：主授课程、助教名称备注等
}
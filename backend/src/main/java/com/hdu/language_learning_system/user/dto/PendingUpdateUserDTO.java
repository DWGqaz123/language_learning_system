package com.hdu.language_learning_system.user.dto;

import lombok.Data;

@Data
public class PendingUpdateUserDTO {
    private Integer userId;
    private String username;
    private String phoneNumber;
    private String roleName;
    private String pendingUpdateJson; // 原始JSON内容（前端可以选择是否解析）
}
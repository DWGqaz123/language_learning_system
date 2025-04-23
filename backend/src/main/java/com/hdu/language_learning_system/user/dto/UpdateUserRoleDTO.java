package com.hdu.language_learning_system.user.dto;

import lombok.Data;

@Data
public class UpdateUserRoleDTO {
    private Integer userId;   // 要更新权限的用户ID
    private Integer roleId;   // 新的角色ID
    private Integer operatorUserId; // 发起操作的用户ID
}

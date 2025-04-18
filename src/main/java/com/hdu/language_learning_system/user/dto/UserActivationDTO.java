package com.hdu.language_learning_system.user.dto;

import lombok.Data;

@Data
public class UserActivationDTO {
    private Integer userId;
    private String newPassword;
}

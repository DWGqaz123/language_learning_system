package com.hdu.language_learning_system.user.dto;

import lombok.Data;

@Data
public class UpdateUserInfoDTO {
    private Integer userId;
    private String username;
    private String phoneNumber;
    private String description;
}

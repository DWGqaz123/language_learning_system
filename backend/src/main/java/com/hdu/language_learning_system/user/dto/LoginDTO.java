package com.hdu.language_learning_system.user.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String phoneNumber;
    private String password;
}
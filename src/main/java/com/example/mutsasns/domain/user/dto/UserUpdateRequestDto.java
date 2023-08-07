package com.example.mutsasns.domain.user.dto;

import lombok.Data;

@Data
public class UserUpdateRequestDto {
    private String oldPassword;
    private String newPassword;
    private String passwordCheck;
    private String email;
    private String phone;
}

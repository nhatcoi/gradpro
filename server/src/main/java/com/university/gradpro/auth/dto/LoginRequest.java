package com.university.gradpro.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    
    @NotBlank(message = "Email hoặc mã số không được để trống")
    private String emailOrCode;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}

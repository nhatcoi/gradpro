package com.university.gradpro.user.dto;

import com.university.gradpro.common.constant.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
    
    @NotBlank(message = "Mã số không được để trống")
    @Size(max = 50, message = "Mã số không được quá 50 ký tự")
    private String code;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;
    
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được quá 100 ký tự")
    private String fullName;
    
    @Size(max = 20, message = "Số điện thoại không được quá 20 ký tự")
    private String phone;
    
    @NotNull(message = "Vai trò không được để trống")
    private RoleType role;
    
    private String department;
    private String major;
    private String academicYear;
    private String academicTitle;
}

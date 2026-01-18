package com.university.gradpro.user.dto;

import com.university.gradpro.common.constant.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
    
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @Size(max = 100, message = "Họ tên không được quá 100 ký tự")
    private String fullName;
    
    @Size(max = 20, message = "Số điện thoại không được quá 20 ký tự")
    private String phone;
    
    private RoleType role;
    private String department;
    private String major;
    private String academicYear;
    private String academicTitle;
    private Boolean active;
}

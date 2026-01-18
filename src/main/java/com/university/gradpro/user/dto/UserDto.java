package com.university.gradpro.user.dto;

import com.university.gradpro.common.constant.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private Long id;
    private String code;
    private String email;
    private String fullName;
    private String phone;
    private RoleType role;
    private String department;
    private String major;
    private String academicYear;
    private String academicTitle;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

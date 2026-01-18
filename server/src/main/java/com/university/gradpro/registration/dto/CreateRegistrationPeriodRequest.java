package com.university.gradpro.registration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateRegistrationPeriodRequest {
    
    @NotBlank(message = "Tên đợt đăng ký không được để trống")
    private String name;
    
    @NotBlank(message = "Học kỳ không được để trống")
    private String semester;
    
    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;
    
    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;
    
    private String description;
}

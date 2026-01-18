package com.university.gradpro.council.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class AddScheduleRequest {
    
    @NotNull(message = "Vui lòng chọn đề tài")
    private Long topicId;
    
    @NotNull(message = "Vui lòng chọn sinh viên")
    private Long studentId;
    
    @NotNull(message = "Thứ tự bảo vệ không được để trống")
    private Integer orderNumber;
    
    private LocalTime startTime;
    private LocalTime endTime;
    private String notes;
}

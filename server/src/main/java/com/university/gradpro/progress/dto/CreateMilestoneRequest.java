package com.university.gradpro.progress.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateMilestoneRequest {
    
    @NotNull(message = "Vui lòng chọn đề tài")
    private Long topicId;
    
    @NotBlank(message = "Tên giai đoạn không được để trống")
    private String title;
    
    private String description;
    
    @NotNull(message = "Thứ tự giai đoạn không được để trống")
    private Integer orderIndex;
    
    @NotNull(message = "Ngày deadline không được để trống")
    private LocalDateTime dueDate;
    
    private Double weight = 0.0;
}

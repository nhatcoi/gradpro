package com.university.gradpro.topic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentProposalRequest {
    
    @NotBlank(message = "Tên đề tài không được để trống")
    @Size(max = 500, message = "Tên đề tài không được quá 500 ký tự")
    private String title;
    
    private String description;
    private String requirements;
    private String expectedOutcome;
    private String technology;
    
    @NotNull(message = "Phải chọn giảng viên hướng dẫn")
    private Long supervisorId;
}

package com.university.gradpro.progress.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitProgressRequest {
    
    @NotNull(message = "Vui lòng chọn giai đoạn")
    private Long milestoneId;
    
    private String content;
}

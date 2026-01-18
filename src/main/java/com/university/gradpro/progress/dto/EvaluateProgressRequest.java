package com.university.gradpro.progress.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EvaluateProgressRequest {
    
    @NotNull(message = "Điểm không được để trống")
    @Min(value = 0, message = "Điểm tối thiểu là 0")
    @Max(value = 10, message = "Điểm tối đa là 10")
    private Double score;
    
    private String comment;
    
    private boolean approved = true;
}

package com.university.gradpro.evaluation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateEvaluationRequest {
    
    @NotNull(message = "Vui lòng chọn đề tài")
    private Long topicId;
    
    @NotNull(message = "Vui lòng chọn sinh viên")
    private Long studentId;
    
    private Long councilId;  // Null nếu là điểm GVHD
    
    @NotNull(message = "Điểm không được để trống")
    @Min(value = 0, message = "Điểm tối thiểu là 0")
    @Max(value = 10, message = "Điểm tối đa là 10")
    private Double score;
    
    private String comment;
    private String strengths;
    private String weaknesses;
}

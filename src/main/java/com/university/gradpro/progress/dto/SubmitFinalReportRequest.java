package com.university.gradpro.progress.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitFinalReportRequest {
    
    @NotNull(message = "Vui lòng chọn đề tài")
    private Long topicId;
    
    private String abstractContent;
}

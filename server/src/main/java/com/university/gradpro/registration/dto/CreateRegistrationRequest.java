package com.university.gradpro.registration.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRegistrationRequest {
    
    @NotNull(message = "Vui lòng chọn đề tài")
    private Long topicId;
    
    private String note;
}

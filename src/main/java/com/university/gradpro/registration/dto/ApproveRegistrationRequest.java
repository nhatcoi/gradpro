package com.university.gradpro.registration.dto;

import lombok.Data;

@Data
public class ApproveRegistrationRequest {
    
    private boolean approved;
    private String rejectionReason;
}

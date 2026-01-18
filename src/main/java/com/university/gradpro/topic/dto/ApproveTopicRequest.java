package com.university.gradpro.topic.dto;

import lombok.Data;

@Data
public class ApproveTopicRequest {
    
    private boolean approved;
    private String rejectionReason;
}

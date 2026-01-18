package com.university.gradpro.topic.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateTopicRequest {
    
    @Size(max = 500, message = "Tên đề tài không được quá 500 ký tự")
    private String title;
    
    private String description;
    private String requirements;
    private String expectedOutcome;
    private String technology;
    private Integer maxStudents;
}

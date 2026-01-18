package com.university.gradpro.topic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTopicRequest {
    
    @NotBlank(message = "Tên đề tài không được để trống")
    @Size(max = 500, message = "Tên đề tài không được quá 500 ký tự")
    private String title;
    
    private String description;
    private String requirements;
    private String expectedOutcome;
    private String technology;
    private String department;
    private String major;
    
    @NotBlank(message = "Học kỳ không được để trống")
    private String semester;
    
    private Integer maxStudents = 1;
}

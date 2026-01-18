package com.university.gradpro.progress.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneDto {
    
    private Long id;
    private Long topicId;
    private String topicTitle;
    private String title;
    private String description;
    private Integer orderIndex;
    private LocalDateTime dueDate;
    private Double weight;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

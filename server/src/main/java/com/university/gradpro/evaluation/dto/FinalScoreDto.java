package com.university.gradpro.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalScoreDto {
    
    private Long id;
    private Double supervisorScore;
    private Double councilScore;
    private Double finalScore;
    private String letterGrade;
    private Boolean passed;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private TopicInfo topic;
    private StudentInfo student;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicInfo {
        private Long id;
        private String code;
        private String title;
        private String supervisorName;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentInfo {
        private Long id;
        private String code;
        private String fullName;
        private String major;
    }
}

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
public class EvaluationDto {
    
    private Long id;
    private String evaluationType;
    private Double score;
    private String comment;
    private String strengths;
    private String weaknesses;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private TopicInfo topic;
    private StudentInfo student;
    private EvaluatorInfo evaluator;
    private CouncilInfo council;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicInfo {
        private Long id;
        private String code;
        private String title;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentInfo {
        private Long id;
        private String code;
        private String fullName;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvaluatorInfo {
        private Long id;
        private String code;
        private String fullName;
        private String academicTitle;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouncilInfo {
        private Long id;
        private String code;
        private String name;
    }
}

package com.university.gradpro.progress.dto;

import com.university.gradpro.common.constant.ProgressStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalReportDto {
    
    private Long id;
    private String filePath;
    private String fileName;
    private String abstractContent;
    private ProgressStatus status;
    private Boolean eligibleForDefense;
    private LocalDateTime submittedAt;
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
}

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
public class ProgressReportDto {
    
    private Long id;
    private ProgressStatus status;
    private String content;
    private String filePath;
    private String fileName;
    private String lecturerComment;
    private Double score;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private MilestoneInfo milestone;
    private StudentInfo student;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MilestoneInfo {
        private Long id;
        private String title;
        private LocalDateTime dueDate;
        private Integer orderIndex;
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

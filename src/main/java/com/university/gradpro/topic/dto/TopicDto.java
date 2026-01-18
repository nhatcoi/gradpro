package com.university.gradpro.topic.dto;

import com.university.gradpro.common.constant.TopicStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDto {
    
    private Long id;
    private String code;
    private String title;
    private String description;
    private String requirements;
    private String expectedOutcome;
    private String technology;
    private TopicStatus status;
    private String department;
    private String major;
    private String semester;
    private Integer maxStudents;
    private String rejectionReason;
    
    private LecturerInfo lecturer;
    private LecturerInfo supervisor;
    private StudentInfo student;
    private LecturerInfo approvedBy;
    
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LecturerInfo {
        private Long id;
        private String code;
        private String fullName;
        private String email;
        private String academicTitle;
        private String department;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentInfo {
        private Long id;
        private String code;
        private String fullName;
        private String email;
        private String major;
        private String academicYear;
    }
}

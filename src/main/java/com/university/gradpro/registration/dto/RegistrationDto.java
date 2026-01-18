package com.university.gradpro.registration.dto;

import com.university.gradpro.common.constant.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDto {
    
    private Long id;
    private RegistrationStatus status;
    private String note;
    private String rejectionReason;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private StudentInfo student;
    private TopicInfo topic;
    private PeriodInfo period;
    private ApproverInfo approvedBy;
    
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
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicInfo {
        private Long id;
        private String code;
        private String title;
        private String lecturerName;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodInfo {
        private Long id;
        private String name;
        private String semester;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApproverInfo {
        private Long id;
        private String fullName;
    }
}

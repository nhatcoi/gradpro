package com.university.gradpro.council.dto;

import com.university.gradpro.common.constant.CouncilRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouncilDto {
    
    private Long id;
    private String code;
    private String name;
    private String department;
    private String semester;
    private LocalDateTime defenseDate;
    private String room;
    private String notes;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private List<MemberInfo> members;
    private List<ScheduleInfo> schedules;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        private Long id;
        private Long lecturerId;
        private String lecturerCode;
        private String lecturerName;
        private String academicTitle;
        private CouncilRole role;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleInfo {
        private Long id;
        private Long topicId;
        private String topicCode;
        private String topicTitle;
        private Long studentId;
        private String studentCode;
        private String studentName;
        private Integer orderNumber;
        private String startTime;
        private String endTime;
        private Boolean completed;
    }
}

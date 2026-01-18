package com.university.gradpro.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDto {
    
    private String semester;
    private String department;
    
    // Thống kê đề tài
    private long totalTopics;
    private long approvedTopics;
    private long pendingTopics;
    private long rejectedTopics;
    private long completedTopics;
    
    // Thống kê sinh viên
    private long totalStudents;
    private long registeredStudents;
    private long defendedStudents;
    private long passedStudents;
    private long failedStudents;
    
    // Thống kê giảng viên
    private long totalLecturers;
    private long supervisingLecturers;
    
    // Thống kê điểm
    private Double averageScore;
    private Double passRate;
    
    // Phân bố điểm
    private Map<String, Long> gradeDistribution;
    
    // Thống kê theo khoa/bộ môn
    private List<DepartmentStats> departmentStats;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentStats {
        private String department;
        private long totalTopics;
        private long totalStudents;
        private long passedStudents;
        private Double averageScore;
    }
}

package com.university.gradpro.report.service;

import com.university.gradpro.common.constant.RoleType;
import com.university.gradpro.common.constant.TopicStatus;
import com.university.gradpro.evaluation.repository.FinalScoreRepository;
import com.university.gradpro.report.dto.StatisticsDto;
import com.university.gradpro.topic.repository.TopicRepository;
import com.university.gradpro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {
    
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final FinalScoreRepository finalScoreRepository;
    
    /**
     * UC-2.4: Thống kê cấp trường
     */
    public StatisticsDto getUniversityStatistics(String semester) {
        // Thống kê đề tài
        long totalTopics = topicRepository.count();
        long approvedTopics = topicRepository.countByStatusAndSemester(TopicStatus.APPROVED, semester);
        long pendingTopics = topicRepository.countByStatusAndSemester(TopicStatus.PENDING, semester);
        long rejectedTopics = topicRepository.countByStatusAndSemester(TopicStatus.REJECTED, semester);
        long completedTopics = topicRepository.countByStatusAndSemester(TopicStatus.COMPLETED, semester);
        
        // Thống kê người dùng
        long totalStudents = userRepository.countActiveByRole(RoleType.STUDENT);
        long totalLecturers = userRepository.countActiveByRole(RoleType.LECTURER);
        
        // Thống kê điểm
        long passedStudents = finalScoreRepository.countPassedBySemester(semester);
        long totalDefended = finalScoreRepository.countBySemester(semester);
        Double averageScore = finalScoreRepository.calculateAverageScoreBySemester(semester);
        
        double passRate = totalDefended > 0 ? (passedStudents * 100.0 / totalDefended) : 0;
        
        // Phân bố điểm
        Map<String, Long> gradeDistribution = calculateGradeDistribution(semester);
        
        return StatisticsDto.builder()
                .semester(semester)
                .totalTopics(totalTopics)
                .approvedTopics(approvedTopics)
                .pendingTopics(pendingTopics)
                .rejectedTopics(rejectedTopics)
                .completedTopics(completedTopics)
                .totalStudents(totalStudents)
                .registeredStudents(0) // TODO: Calculate from registrations
                .defendedStudents(totalDefended)
                .passedStudents(passedStudents)
                .failedStudents(totalDefended - passedStudents)
                .totalLecturers(totalLecturers)
                .averageScore(averageScore != null ? averageScore : 0.0)
                .passRate(Math.round(passRate * 100.0) / 100.0)
                .gradeDistribution(gradeDistribution)
                .build();
    }
    
    /**
     * UC-3.5: Thống kê cấp khoa
     */
    public StatisticsDto getDepartmentStatistics(String department, String semester) {
        // Thống kê đề tài theo khoa
        long totalTopics = topicRepository.countByDepartmentAndSemester(department, semester);
        
        // Thống kê người dùng
        long totalStudents = userRepository.findByRoleAndDepartment(RoleType.STUDENT, department).size();
        long totalLecturers = userRepository.findByRoleAndDepartment(RoleType.LECTURER, department).size();
        
        // Thống kê điểm
        var scores = finalScoreRepository.findByDepartmentAndSemester(department, semester);
        long passedStudents = scores.stream().filter(s -> s.getPassed()).count();
        Double averageScore = scores.stream()
                .mapToDouble(s -> s.getFinalScore())
                .average()
                .orElse(0.0);
        
        double passRate = scores.size() > 0 ? (passedStudents * 100.0 / scores.size()) : 0;
        
        return StatisticsDto.builder()
                .semester(semester)
                .department(department)
                .totalTopics(totalTopics)
                .totalStudents(totalStudents)
                .defendedStudents(scores.size())
                .passedStudents(passedStudents)
                .failedStudents(scores.size() - passedStudents)
                .totalLecturers(totalLecturers)
                .averageScore(Math.round(averageScore * 100.0) / 100.0)
                .passRate(Math.round(passRate * 100.0) / 100.0)
                .build();
    }
    
    private Map<String, Long> calculateGradeDistribution(String semester) {
        Map<String, Long> distribution = new HashMap<>();
        var scores = finalScoreRepository.findBySemester(semester);
        
        distribution.put("A+", scores.stream().filter(s -> "A+".equals(s.getLetterGrade())).count());
        distribution.put("A", scores.stream().filter(s -> "A".equals(s.getLetterGrade())).count());
        distribution.put("B+", scores.stream().filter(s -> "B+".equals(s.getLetterGrade())).count());
        distribution.put("B", scores.stream().filter(s -> "B".equals(s.getLetterGrade())).count());
        distribution.put("C+", scores.stream().filter(s -> "C+".equals(s.getLetterGrade())).count());
        distribution.put("C", scores.stream().filter(s -> "C".equals(s.getLetterGrade())).count());
        distribution.put("D+", scores.stream().filter(s -> "D+".equals(s.getLetterGrade())).count());
        distribution.put("D", scores.stream().filter(s -> "D".equals(s.getLetterGrade())).count());
        distribution.put("F", scores.stream().filter(s -> "F".equals(s.getLetterGrade())).count());
        
        return distribution;
    }
}

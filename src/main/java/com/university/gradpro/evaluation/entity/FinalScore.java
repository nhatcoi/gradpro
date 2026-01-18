package com.university.gradpro.evaluation.entity;

import com.university.gradpro.topic.entity.Topic;
import com.university.gradpro.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "final_scores",
       uniqueConstraints = @UniqueConstraint(columnNames = {"topic_id", "student_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalScore {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @Column(nullable = false)
    private Double supervisorScore;  // Điểm GVHD (40%)
    
    @Column(nullable = false)
    private Double councilScore;  // Điểm trung bình HĐ (60%)
    
    @Column(nullable = false)
    private Double finalScore;  // Điểm tổng kết
    
    @Column(length = 5)
    private String letterGrade;  // A, B+, B, C+, C, D+, D, F
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean passed = false;  // Đạt/Không đạt
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

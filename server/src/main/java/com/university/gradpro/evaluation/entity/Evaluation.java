package com.university.gradpro.evaluation.entity;

import com.university.gradpro.council.entity.Council;
import com.university.gradpro.topic.entity.Topic;
import com.university.gradpro.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "evaluations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"topic_id", "student_id", "evaluator_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id", nullable = false)
    private User evaluator;  // Người chấm điểm (GVHD hoặc thành viên HĐ)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "council_id")
    private Council council;  // Hội đồng (nếu là điểm HĐ)
    
    @Column(nullable = false, length = 50)
    private String evaluationType;  // SUPERVISOR (GVHD) hoặc COUNCIL (Hội đồng)
    
    @Column(nullable = false)
    private Double score;  // Điểm số (thang 10)
    
    @Column(columnDefinition = "TEXT")
    private String comment;  // Nhận xét
    
    @Column(columnDefinition = "TEXT")
    private String strengths;  // Điểm mạnh
    
    @Column(columnDefinition = "TEXT")
    private String weaknesses;  // Điểm yếu
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

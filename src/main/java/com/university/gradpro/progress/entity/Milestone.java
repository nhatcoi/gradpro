package com.university.gradpro.progress.entity;

import com.university.gradpro.topic.entity.Topic;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "milestones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Milestone {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;
    
    @Column(nullable = false, length = 200)
    private String title;  // Tên giai đoạn
    
    @Column(columnDefinition = "TEXT")
    private String description;  // Mô tả yêu cầu
    
    @Column(nullable = false)
    private Integer orderIndex;  // Thứ tự giai đoạn
    
    @Column(nullable = false)
    private LocalDateTime dueDate;  // Deadline
    
    @Builder.Default
    @Column(nullable = false)
    private Double weight = 0.0;  // Trọng số điểm (%)
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

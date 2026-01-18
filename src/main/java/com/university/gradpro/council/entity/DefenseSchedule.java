package com.university.gradpro.council.entity;

import com.university.gradpro.topic.entity.Topic;
import com.university.gradpro.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "defense_schedules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefenseSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "council_id", nullable = false)
    private Council council;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @Column(nullable = false)
    private Integer orderNumber;  // Thứ tự bảo vệ trong ngày
    
    private LocalTime startTime;  // Giờ bắt đầu
    
    private LocalTime endTime;  // Giờ kết thúc
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean completed = false;  // Đã bảo vệ xong
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

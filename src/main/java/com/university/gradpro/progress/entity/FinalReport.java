package com.university.gradpro.progress.entity;

import com.university.gradpro.common.constant.ProgressStatus;
import com.university.gradpro.topic.entity.Topic;
import com.university.gradpro.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "final_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @Column(length = 500)
    private String filePath;  // Đường dẫn file báo cáo
    
    @Column(length = 255)
    private String fileName;  // Tên file gốc
    
    @Column(columnDefinition = "TEXT")
    private String abstractContent;  // Tóm tắt nội dung
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProgressStatus status = ProgressStatus.SUBMITTED;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean eligibleForDefense = false;  // Đủ điều kiện bảo vệ
    
    private LocalDateTime submittedAt;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

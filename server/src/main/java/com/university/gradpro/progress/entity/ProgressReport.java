package com.university.gradpro.progress.entity;

import com.university.gradpro.common.constant.ProgressStatus;
import com.university.gradpro.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "progress_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id", nullable = false)
    private Milestone milestone;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @Column(columnDefinition = "TEXT")
    private String content;  // Nội dung báo cáo
    
    @Column(length = 500)
    private String filePath;  // Đường dẫn file đính kèm
    
    @Column(length = 255)
    private String fileName;  // Tên file gốc
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProgressStatus status = ProgressStatus.NOT_STARTED;
    
    @Column(columnDefinition = "TEXT")
    private String lecturerComment;  // Nhận xét của GVHD
    
    private Double score;  // Điểm đánh giá
    
    private LocalDateTime submittedAt;  // Thời gian nộp
    
    private LocalDateTime reviewedAt;  // Thời gian đánh giá
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

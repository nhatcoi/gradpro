package com.university.gradpro.topic.entity;

import com.university.gradpro.common.constant.TopicStatus;
import com.university.gradpro.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "topics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Topic {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String code;  // Mã đề tài
    
    @Column(nullable = false, length = 500)
    private String title;  // Tên đề tài
    
    @Column(columnDefinition = "TEXT")
    private String description;  // Mô tả chi tiết
    
    @Column(columnDefinition = "TEXT")
    private String requirements;  // Yêu cầu
    
    @Column(columnDefinition = "TEXT")
    private String expectedOutcome;  // Kết quả mong đợi
    
    @Column(length = 100)
    private String technology;  // Công nghệ sử dụng
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id")
    private User lecturer;  // Giảng viên đề xuất
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;  // Sinh viên đề xuất (nếu có)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private User supervisor;  // GVHD được phân công
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;  // Trưởng BM duyệt
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TopicStatus status = TopicStatus.PENDING;
    
    @Column(length = 100)
    private String department;  // Khoa
    
    @Column(length = 100)
    private String major;  // Bộ môn/Chuyên ngành
    
    @Column(length = 20)
    private String semester;  // Học kỳ (VD: "HK1-2025-2026")
    
    @Builder.Default
    @Column(nullable = false)
    private Integer maxStudents = 1;  // Số SV tối đa
    
    @Column(columnDefinition = "TEXT")
    private String rejectionReason;  // Lý do từ chối (nếu có)
    
    private LocalDateTime approvedAt;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

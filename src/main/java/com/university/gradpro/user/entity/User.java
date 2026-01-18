package com.university.gradpro.user.entity;

import com.university.gradpro.common.constant.RoleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String code;  // Mã số (MSSV hoặc Mã GV)
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, length = 100)
    private String fullName;
    
    @Column(length = 20)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;
    
    @Column(length = 100)
    private String department;  // Khoa
    
    @Column(length = 100)
    private String major;  // Chuyên ngành/Bộ môn
    
    @Column(length = 50)
    private String academicYear;  // Khóa học (cho sinh viên)
    
    @Column(length = 100)
    private String academicTitle;  // Học vị (cho giảng viên): ThS, TS, PGS, GS
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

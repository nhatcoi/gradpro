package com.university.gradpro.registration.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "registration_periods")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationPeriod {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;  // Tên đợt đăng ký
    
    @Column(nullable = false, length = 20)
    private String semester;  // Học kỳ
    
    @Column(nullable = false)
    private LocalDateTime startDate;  // Ngày bắt đầu
    
    @Column(nullable = false)
    private LocalDateTime endDate;  // Ngày kết thúc
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public boolean isOpen() {
        LocalDateTime now = LocalDateTime.now();
        return active && now.isAfter(startDate) && now.isBefore(endDate);
    }
}

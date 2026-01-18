package com.university.gradpro.council.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "councils")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Council {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String code;  // Mã hội đồng
    
    @Column(nullable = false, length = 200)
    private String name;  // Tên hội đồng
    
    @Column(length = 100)
    private String department;  // Khoa
    
    @Column(nullable = false, length = 20)
    private String semester;  // Học kỳ
    
    private LocalDateTime defenseDate;  // Ngày bảo vệ
    
    @Column(length = 100)
    private String room;  // Phòng bảo vệ
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
    
    @OneToMany(mappedBy = "council", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CouncilMember> members = new ArrayList<>();
    
    @OneToMany(mappedBy = "council", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DefenseSchedule> schedules = new ArrayList<>();
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

package com.university.gradpro.council.entity;

import com.university.gradpro.common.constant.CouncilRole;
import com.university.gradpro.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "council_members",
       uniqueConstraints = @UniqueConstraint(columnNames = {"council_id", "lecturer_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouncilMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "council_id", nullable = false)
    private Council council;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id", nullable = false)
    private User lecturer;  // Giảng viên trong hội đồng
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouncilRole role;  // Vai trò trong hội đồng
}

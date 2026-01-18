package com.university.gradpro.registration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationPeriodDto {
    
    private Long id;
    private String name;
    private String semester;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private Boolean active;
    private Boolean isOpen;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

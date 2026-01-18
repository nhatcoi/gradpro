package com.university.gradpro.council.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateCouncilRequest {
    
    @NotBlank(message = "Tên hội đồng không được để trống")
    private String name;
    
    private String department;
    
    @NotBlank(message = "Học kỳ không được để trống")
    private String semester;
    
    @NotNull(message = "Ngày bảo vệ không được để trống")
    private LocalDateTime defenseDate;
    
    private String room;
    private String notes;
    
    private List<MemberRequest> members;
    
    @Data
    public static class MemberRequest {
        private Long lecturerId;
        private String role;  // CHAIRMAN, SECRETARY, REVIEWER, MEMBER
    }
}

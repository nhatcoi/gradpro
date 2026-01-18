package com.university.gradpro.council.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AssignCouncilRequest {
    
    @NotNull(message = "Vui lòng chọn hội đồng")
    private Long councilId;
    
    @NotNull(message = "Danh sách đề tài không được để trống")
    private List<Long> topicIds;
}

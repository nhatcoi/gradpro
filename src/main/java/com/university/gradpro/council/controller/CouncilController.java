package com.university.gradpro.council.controller;

import com.university.gradpro.auth.security.UserPrincipal;
import com.university.gradpro.common.response.ApiResponse;
import com.university.gradpro.council.dto.*;
import com.university.gradpro.council.service.CouncilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/councils")
@RequiredArgsConstructor
public class CouncilController {
    
    private final CouncilService councilService;
    
    /**
     * UC-3.3: Tạo hội đồng
     */
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<CouncilDto>> createCouncil(
            @Valid @RequestBody CreateCouncilRequest request) {
        CouncilDto council = councilService.createCouncil(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo hội đồng thành công", council));
    }
    
    /**
     * Cập nhật hội đồng
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPT_HEAD', 'TRAINING_DEPT')")
    public ResponseEntity<ApiResponse<CouncilDto>> updateCouncil(
            @PathVariable Long id,
            @Valid @RequestBody CreateCouncilRequest request) {
        CouncilDto council = councilService.updateCouncil(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật hội đồng thành công", council));
    }
    
    /**
     * Thêm thành viên hội đồng
     */
    @PostMapping("/{id}/members")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<CouncilDto>> addMember(
            @PathVariable Long id,
            @RequestParam Long lecturerId,
            @RequestParam String role) {
        CouncilDto council = councilService.addCouncilMember(id, lecturerId, role);
        return ResponseEntity.ok(ApiResponse.success("Thêm thành viên thành công", council));
    }
    
    /**
     * Xóa thành viên hội đồng
     */
    @DeleteMapping("/{id}/members/{lecturerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<CouncilDto>> removeMember(
            @PathVariable Long id,
            @PathVariable Long lecturerId) {
        CouncilDto council = councilService.removeCouncilMember(id, lecturerId);
        return ResponseEntity.ok(ApiResponse.success("Xóa thành viên thành công", council));
    }
    
    /**
     * UC-2.3: Xếp lịch - Thêm lịch bảo vệ
     */
    @PostMapping("/{id}/schedules")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINING_DEPT')")
    public ResponseEntity<ApiResponse<CouncilDto>> addSchedule(
            @PathVariable Long id,
            @Valid @RequestBody AddScheduleRequest request) {
        CouncilDto council = councilService.addDefenseSchedule(id, request);
        return ResponseEntity.ok(ApiResponse.success("Thêm lịch bảo vệ thành công", council));
    }
    
    /**
     * UC-2.2: Phân công hội đồng
     */
    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINING_DEPT')")
    public ResponseEntity<ApiResponse<CouncilDto>> assignTopics(
            @Valid @RequestBody AssignCouncilRequest request) {
        CouncilDto council = councilService.assignTopicsToCouncil(request);
        return ResponseEntity.ok(ApiResponse.success("Phân công đề tài vào hội đồng thành công", council));
    }
    
    /**
     * Đánh dấu hoàn thành bảo vệ
     */
    @PatchMapping("/schedules/{scheduleId}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINING_DEPT', 'DEPT_HEAD', 'LECTURER')")
    public ResponseEntity<ApiResponse<Void>> markComplete(@PathVariable Long scheduleId) {
        councilService.markDefenseCompleted(scheduleId);
        return ResponseEntity.ok(ApiResponse.success("Đánh dấu hoàn thành bảo vệ"));
    }
    
    /**
     * Lấy thông tin hội đồng
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CouncilDto>> getCouncilById(@PathVariable Long id) {
        CouncilDto council = councilService.getCouncilById(id);
        return ResponseEntity.ok(ApiResponse.success(council));
    }
    
    /**
     * Lấy hội đồng theo mã
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<CouncilDto>> getCouncilByCode(@PathVariable String code) {
        CouncilDto council = councilService.getCouncilByCode(code);
        return ResponseEntity.ok(ApiResponse.success(council));
    }
    
    /**
     * Lấy danh sách hội đồng theo học kỳ
     */
    @GetMapping("/semester/{semester}")
    public ResponseEntity<ApiResponse<List<CouncilDto>>> getCouncilsBySemester(@PathVariable String semester) {
        List<CouncilDto> councils = councilService.getCouncilsBySemester(semester);
        return ResponseEntity.ok(ApiResponse.success(councils));
    }
    
    /**
     * Lấy hội đồng của giảng viên
     */
    @GetMapping("/my-councils")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<List<CouncilDto>>> getMyCouncils(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<CouncilDto> councils = councilService.getCouncilsByLecturer(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(councils));
    }
}

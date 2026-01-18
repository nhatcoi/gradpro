package com.university.gradpro.progress.controller;

import com.university.gradpro.auth.security.UserPrincipal;
import com.university.gradpro.common.response.ApiResponse;
import com.university.gradpro.common.response.PageResponse;
import com.university.gradpro.progress.dto.*;
import com.university.gradpro.progress.service.FinalReportService;
import com.university.gradpro.progress.service.MilestoneService;
import com.university.gradpro.progress.service.ProgressReportService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProgressController {
    
    private final MilestoneService milestoneService;
    private final ProgressReportService progressReportService;
    private final FinalReportService finalReportService;
    
    // ==================== MILESTONE ENDPOINTS ====================
    
    /**
     * UC-4.4: Thiết lập mốc thời gian
     */
    @PostMapping("/milestones")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<MilestoneDto>> createMilestone(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CreateMilestoneRequest request) {
        MilestoneDto milestone = milestoneService.createMilestone(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Tạo mốc thời gian thành công", milestone));
    }
    
    /**
     * Cập nhật mốc thời gian
     */
    @PutMapping("/milestones/{id}")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<MilestoneDto>> updateMilestone(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CreateMilestoneRequest request) {
        MilestoneDto milestone = milestoneService.updateMilestone(id, currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật mốc thời gian thành công", milestone));
    }
    
    /**
     * Xóa mốc thời gian
     */
    @DeleteMapping("/milestones/{id}")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<Void>> deleteMilestone(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        milestoneService.deleteMilestone(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Xóa mốc thời gian thành công"));
    }
    
    /**
     * Lấy các mốc của đề tài
     */
    @GetMapping("/milestones/topic/{topicId}")
    public ResponseEntity<ApiResponse<List<MilestoneDto>>> getMilestonesByTopic(@PathVariable Long topicId) {
        List<MilestoneDto> milestones = milestoneService.getMilestonesByTopic(topicId);
        return ResponseEntity.ok(ApiResponse.success(milestones));
    }
    
    /**
     * Lấy thông tin mốc
     */
    @GetMapping("/milestones/{id}")
    public ResponseEntity<ApiResponse<MilestoneDto>> getMilestoneById(@PathVariable Long id) {
        MilestoneDto milestone = milestoneService.getMilestoneById(id);
        return ResponseEntity.ok(ApiResponse.success(milestone));
    }
    
    // ==================== PROGRESS REPORT ENDPOINTS ====================
    
    /**
     * UC-5.4, UC-5.5: Sinh viên nộp báo cáo tiến độ
     */
    @PostMapping("/progress/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ProgressReportDto>> submitProgress(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestPart("data") SubmitProgressRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        ProgressReportDto progress = progressReportService.submitProgress(currentUser.getId(), request, file);
        return ResponseEntity.ok(ApiResponse.success("Nộp báo cáo tiến độ thành công", progress));
    }
    
    /**
     * UC-4.5: Giảng viên đánh giá tiến độ
     */
    @PostMapping("/progress/evaluate/{id}")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<ProgressReportDto>> evaluateProgress(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody EvaluateProgressRequest request) {
        ProgressReportDto progress = progressReportService.evaluateProgress(id, currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Đánh giá tiến độ thành công", progress));
    }
    
    /**
     * Lấy báo cáo tiến độ của sinh viên
     */
    @GetMapping("/progress/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<ProgressReportDto>>> getMyProgress(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<ProgressReportDto> progress = progressReportService.getProgressByStudent(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(progress));
    }
    
    /**
     * Lấy báo cáo tiến độ theo đề tài
     */
    @GetMapping("/progress/topic/{topicId}")
    public ResponseEntity<ApiResponse<List<ProgressReportDto>>> getProgressByTopic(@PathVariable Long topicId) {
        List<ProgressReportDto> progress = progressReportService.getProgressByTopic(topicId);
        return ResponseEntity.ok(ApiResponse.success(progress));
    }
    
    /**
     * Lấy báo cáo chờ đánh giá của GVHD
     */
    @GetMapping("/progress/pending")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<PageResponse<ProgressReportDto>>> getPendingProgress(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
        PageResponse<ProgressReportDto> progress = progressReportService.getPendingProgressBySupervisor(
                currentUser.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(progress));
    }
    
    // ==================== FINAL REPORT ENDPOINTS ====================
    
    /**
     * UC-5.6: Sinh viên nộp báo cáo cuối
     */
    @PostMapping("/final-reports/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<FinalReportDto>> submitFinalReport(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestPart("data") SubmitFinalReportRequest request,
            @RequestPart("file") MultipartFile file) {
        FinalReportDto report = finalReportService.submitFinalReport(currentUser.getId(), request, file);
        return ResponseEntity.ok(ApiResponse.success("Nộp báo cáo cuối kỳ thành công", report));
    }
    
    /**
     * Đánh dấu đủ điều kiện bảo vệ
     */
    @PatchMapping("/final-reports/{id}/eligible")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<FinalReportDto>> markEligible(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam boolean eligible) {
        FinalReportDto report = finalReportService.markEligibleForDefense(id, currentUser.getId(), eligible);
        String message = eligible ? "Xác nhận đủ điều kiện bảo vệ" : "Đánh dấu không đủ điều kiện bảo vệ";
        return ResponseEntity.ok(ApiResponse.success(message, report));
    }
    
    /**
     * Lấy báo cáo cuối của sinh viên
     */
    @GetMapping("/final-reports/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<FinalReportDto>>> getMyFinalReports(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<FinalReportDto> reports = finalReportService.getFinalReportsByStudent(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(reports));
    }
    
    /**
     * Lấy báo cáo cuối theo GVHD
     */
    @GetMapping("/final-reports/supervisor")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<Page<FinalReportDto>>> getSupervisorFinalReports(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
        Page<FinalReportDto> reports = finalReportService.getFinalReportsBySupervisor(currentUser.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(reports));
    }
    
    /**
     * Lấy các báo cáo đủ điều kiện bảo vệ
     */
    @GetMapping("/final-reports/eligible")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINING_DEPT', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<List<FinalReportDto>>> getEligibleReports(
            @RequestParam String semester) {
        List<FinalReportDto> reports = finalReportService.getEligibleReports(semester);
        return ResponseEntity.ok(ApiResponse.success(reports));
    }
}

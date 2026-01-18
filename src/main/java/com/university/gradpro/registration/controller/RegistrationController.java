package com.university.gradpro.registration.controller;

import com.university.gradpro.auth.security.UserPrincipal;
import com.university.gradpro.common.constant.RegistrationStatus;
import com.university.gradpro.common.response.ApiResponse;
import com.university.gradpro.common.response.PageResponse;
import com.university.gradpro.registration.dto.*;
import com.university.gradpro.registration.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {
    
    private final RegistrationService registrationService;
    
    /**
     * UC-5.3: Sinh viên đăng ký đề tài
     */
    @PostMapping("/register")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<RegistrationDto>> registerTopic(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CreateRegistrationRequest request) {
        RegistrationDto registration = registrationService.registerTopic(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Đăng ký đề tài thành công", registration));
    }
    
    /**
     * UC-4.3: Giảng viên chọn sinh viên
     */
    @PostMapping("/approve/{id}")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<RegistrationDto>> approveRegistration(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ApproveRegistrationRequest request) {
        RegistrationDto registration = registrationService.approveRegistration(id, currentUser.getId(), request);
        String message = request.isApproved() ? "Chấp nhận sinh viên thành công" : "Từ chối sinh viên thành công";
        return ResponseEntity.ok(ApiResponse.success(message, registration));
    }
    
    /**
     * Hủy đăng ký (sinh viên)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Void>> cancelRegistration(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        registrationService.cancelRegistration(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Hủy đăng ký thành công"));
    }
    
    /**
     * Lấy thông tin đăng ký
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RegistrationDto>> getRegistrationById(@PathVariable Long id) {
        RegistrationDto registration = registrationService.getRegistrationById(id);
        return ResponseEntity.ok(ApiResponse.success(registration));
    }
    
    /**
     * Lấy đăng ký của sinh viên đang đăng nhập
     */
    @GetMapping("/my-registrations")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<RegistrationDto>>> getMyRegistrations(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<RegistrationDto> registrations = registrationService.getRegistrationsByStudent(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(registrations));
    }
    
    /**
     * Lấy đăng ký theo đề tài của giảng viên
     */
    @GetMapping("/lecturer")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<PageResponse<RegistrationDto>>> getLecturerRegistrations(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(required = false) RegistrationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<RegistrationDto> registrations = registrationService.getRegistrationsByLecturer(
                currentUser.getId(), status, pageable);
        return ResponseEntity.ok(ApiResponse.success(registrations));
    }
    
    /**
     * Lấy tất cả đăng ký theo trạng thái
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINING_DEPT', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<PageResponse<RegistrationDto>>> getRegistrationsByStatus(
            @PathVariable RegistrationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<RegistrationDto> registrations = registrationService.getRegistrationsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(registrations));
    }
}

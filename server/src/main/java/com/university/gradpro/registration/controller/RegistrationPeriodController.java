package com.university.gradpro.registration.controller;

import com.university.gradpro.common.response.ApiResponse;
import com.university.gradpro.registration.dto.CreateRegistrationPeriodRequest;
import com.university.gradpro.registration.dto.RegistrationPeriodDto;
import com.university.gradpro.registration.service.RegistrationPeriodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registration-periods")
@RequiredArgsConstructor
public class RegistrationPeriodController {
    
    private final RegistrationPeriodService periodService;
    
    /**
     * UC-2.1: Mở đợt đăng ký
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINING_DEPT')")
    public ResponseEntity<ApiResponse<RegistrationPeriodDto>> createPeriod(
            @Valid @RequestBody CreateRegistrationPeriodRequest request) {
        RegistrationPeriodDto period = periodService.createPeriod(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo đợt đăng ký thành công", period));
    }
    
    /**
     * Cập nhật đợt đăng ký
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINING_DEPT')")
    public ResponseEntity<ApiResponse<RegistrationPeriodDto>> updatePeriod(
            @PathVariable Long id,
            @Valid @RequestBody CreateRegistrationPeriodRequest request) {
        RegistrationPeriodDto period = periodService.updatePeriod(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật đợt đăng ký thành công", period));
    }
    
    /**
     * Đóng đợt đăng ký
     */
    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINING_DEPT')")
    public ResponseEntity<ApiResponse<RegistrationPeriodDto>> closePeriod(@PathVariable Long id) {
        RegistrationPeriodDto period = periodService.closePeriod(id);
        return ResponseEntity.ok(ApiResponse.success("Đóng đợt đăng ký thành công", period));
    }
    
    /**
     * Mở lại đợt đăng ký
     */
    @PatchMapping("/{id}/open")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINING_DEPT')")
    public ResponseEntity<ApiResponse<RegistrationPeriodDto>> openPeriod(@PathVariable Long id) {
        RegistrationPeriodDto period = periodService.openPeriod(id);
        return ResponseEntity.ok(ApiResponse.success("Mở đợt đăng ký thành công", period));
    }
    
    /**
     * Lấy thông tin đợt đăng ký
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RegistrationPeriodDto>> getPeriodById(@PathVariable Long id) {
        RegistrationPeriodDto period = periodService.getPeriodById(id);
        return ResponseEntity.ok(ApiResponse.success(period));
    }
    
    /**
     * Lấy danh sách đợt đăng ký
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RegistrationPeriodDto>>> getAllPeriods() {
        List<RegistrationPeriodDto> periods = periodService.getAllPeriods();
        return ResponseEntity.ok(ApiResponse.success(periods));
    }
    
    /**
     * Lấy đợt đăng ký hiện tại
     */
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<RegistrationPeriodDto>> getCurrentPeriod() {
        return periodService.getCurrentOpenPeriod()
                .map(period -> ResponseEntity.ok(ApiResponse.success(period)))
                .orElse(ResponseEntity.ok(ApiResponse.success("Không có đợt đăng ký nào đang mở", null)));
    }
}

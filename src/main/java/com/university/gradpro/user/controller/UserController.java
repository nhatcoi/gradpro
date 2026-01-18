package com.university.gradpro.user.controller;

import com.university.gradpro.auth.security.UserPrincipal;
import com.university.gradpro.common.constant.RoleType;
import com.university.gradpro.common.response.ApiResponse;
import com.university.gradpro.common.response.PageResponse;
import com.university.gradpro.user.dto.*;
import com.university.gradpro.user.service.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * UC-03: Cập nhật thông tin cá nhân
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserDto user = userService.updateProfile(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin thành công", user));
    }
    
    /**
     * Lấy thông tin profile
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> getProfile(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        UserDto user = userService.getUserById(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    /**
     * UC-06.1: Lấy danh sách người dùng (Admin)
     */
    @GetMapping("/manage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserDto>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<UserDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    /**
     * UC-06.1: Tạo mới người dùng (Admin)
     */
    @PostMapping("/manage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDto user = userService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo người dùng thành công", user));
    }
    
    /**
     * UC-06.1: Cập nhật người dùng (Admin)
     */
    @PutMapping("/manage/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserDto user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật người dùng thành công", user));
    }
    
    /**
     * UC-06.1: Xóa người dùng (Admin)
     */
    @DeleteMapping("/manage/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa người dùng thành công"));
    }
    
    /**
     * UC-06.1: Vô hiệu hóa tài khoản (Admin)
     */
    @PatchMapping("/manage/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> deactivateUser(@PathVariable Long id) {
        UserDto user = userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("Vô hiệu hóa tài khoản thành công", user));
    }
    
    /**
     * UC-06.1: Kích hoạt tài khoản (Admin)
     */
    @PatchMapping("/manage/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> activateUser(@PathVariable Long id) {
        UserDto user = userService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.success("Kích hoạt tài khoản thành công", user));
    }
    
    /**
     * UC-06.1: Reset mật khẩu (Admin)
     */
    @PatchMapping("/manage/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable Long id,
            @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return ResponseEntity.ok(ApiResponse.success("Reset mật khẩu thành công"));
    }
    
    /**
     * Lấy danh sách người dùng theo vai trò
     */
    @GetMapping("/manage/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserDto>>> getUsersByRole(
            @PathVariable RoleType role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<UserDto> users = userService.getUsersByRole(role, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    /**
     * Tìm kiếm người dùng
     */
    @GetMapping("/manage/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserDto>>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<UserDto> users = userService.searchUsers(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    /**
     * Lấy danh sách giảng viên
     */
    @GetMapping("/lecturers")
    public ResponseEntity<ApiResponse<List<UserDto>>> getLecturers() {
        List<UserDto> lecturers = userService.getLecturers();
        return ResponseEntity.ok(ApiResponse.success(lecturers));
    }
    
    /**
     * Lấy danh sách sinh viên
     */
    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINING_DEPT', 'DEPT_HEAD', 'LECTURER')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getStudents() {
        List<UserDto> students = userService.getStudents();
        return ResponseEntity.ok(ApiResponse.success(students));
    }
}

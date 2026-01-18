package com.university.gradpro.topic.controller;

import com.university.gradpro.auth.security.UserPrincipal;
import com.university.gradpro.common.constant.TopicStatus;
import com.university.gradpro.common.response.ApiResponse;
import com.university.gradpro.common.response.PageResponse;
import com.university.gradpro.topic.dto.*;
import com.university.gradpro.topic.service.TopicService;
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
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {
    
    private final TopicService topicService;
    
    /**
     * UC-4.1: Giảng viên đăng ký đề tài mở
     */
    @PostMapping("/propose")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<TopicDto>> createTopicByLecturer(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CreateTopicRequest request) {
        TopicDto topic = topicService.createTopicByLecturer(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Đăng ký đề tài thành công", topic));
    }
    
    /**
     * UC-5.1: Sinh viên đề xuất đề tài
     */
    @PostMapping("/student-propose")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<TopicDto>> createTopicByStudent(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody StudentProposalRequest request) {
        TopicDto topic = topicService.createTopicByStudent(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Đề xuất đề tài thành công", topic));
    }
    
    /**
     * UC-3.1: Trưởng BM phê duyệt đề tài
     */
    @PostMapping("/approve/{id}")
    @PreAuthorize("hasAnyRole('DEPT_HEAD', 'ADMIN')")
    public ResponseEntity<ApiResponse<TopicDto>> approveTopic(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ApproveTopicRequest request) {
        TopicDto topic = topicService.approveTopic(id, currentUser.getId(), request);
        String message = request.isApproved() ? "Phê duyệt đề tài thành công" : "Từ chối đề tài thành công";
        return ResponseEntity.ok(ApiResponse.success(message, topic));
    }
    
    /**
     * UC-3.2: Phân công GVHD
     */
    @PostMapping("/assign-supervisor/{id}")
    @PreAuthorize("hasAnyRole('DEPT_HEAD', 'ADMIN')")
    public ResponseEntity<ApiResponse<TopicDto>> assignSupervisor(
            @PathVariable Long id,
            @RequestParam Long supervisorId) {
        TopicDto topic = topicService.assignSupervisor(id, supervisorId);
        return ResponseEntity.ok(ApiResponse.success("Phân công GVHD thành công", topic));
    }
    
    /**
     * Cập nhật đề tài
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TopicDto>> updateTopic(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody UpdateTopicRequest request) {
        TopicDto topic = topicService.updateTopic(id, currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật đề tài thành công", topic));
    }
    
    /**
     * Lấy thông tin đề tài
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TopicDto>> getTopicById(@PathVariable Long id) {
        TopicDto topic = topicService.getTopicById(id);
        return ResponseEntity.ok(ApiResponse.success(topic));
    }
    
    /**
     * Lấy đề tài theo mã
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<TopicDto>> getTopicByCode(@PathVariable String code) {
        TopicDto topic = topicService.getTopicByCode(code);
        return ResponseEntity.ok(ApiResponse.success(topic));
    }
    
    /**
     * UC-4.2, UC-5.2: Tìm kiếm đề tài
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<TopicDto>>> searchTopics(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<TopicDto> topics = topicService.searchTopics(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(topics));
    }
    
    /**
     * Lấy đề tài có sẵn để đăng ký (Public)
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<PageResponse<TopicDto>>> getAvailableTopics(
            @RequestParam(required = false) String semester,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<TopicDto> topics = topicService.getAvailableTopics(semester, pageable);
        return ResponseEntity.ok(ApiResponse.success(topics));
    }
    
    /**
     * Lấy đề tài theo trạng thái
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINING_DEPT', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<PageResponse<TopicDto>>> getTopicsByStatus(
            @PathVariable TopicStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<TopicDto> topics = topicService.getTopicsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(topics));
    }
    
    /**
     * Lấy đề tài của giảng viên đang đăng nhập
     */
    @GetMapping("/my-topics")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<List<TopicDto>>> getMyTopics(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<TopicDto> topics = topicService.getTopicsByLecturer(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(topics));
    }
    
    /**
     * Lấy đề tài mà giảng viên đang hướng dẫn
     */
    @GetMapping("/supervising")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<List<TopicDto>>> getSupervisingTopics(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<TopicDto> topics = topicService.getTopicsBySupervisor(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(topics));
    }
}

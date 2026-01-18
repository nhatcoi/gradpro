package com.university.gradpro.evaluation.controller;

import com.university.gradpro.auth.security.UserPrincipal;
import com.university.gradpro.common.response.ApiResponse;
import com.university.gradpro.evaluation.dto.*;
import com.university.gradpro.evaluation.service.EvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
public class EvaluationController {
    
    private final EvaluationService evaluationService;
    
    /**
     * GVHD chấm điểm hướng dẫn
     */
    @PostMapping("/supervisor")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<EvaluationDto>> submitSupervisorEvaluation(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CreateEvaluationRequest request) {
        EvaluationDto evaluation = evaluationService.submitSupervisorEvaluation(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Chấm điểm hướng dẫn thành công", evaluation));
    }
    
    /**
     * UC-4.7: Thành viên hội đồng chấm điểm
     */
    @PostMapping("/council")
    @PreAuthorize("hasAnyRole('LECTURER', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<EvaluationDto>> submitCouncilEvaluation(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CreateEvaluationRequest request) {
        EvaluationDto evaluation = evaluationService.submitCouncilEvaluation(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Chấm điểm hội đồng thành công", evaluation));
    }
    
    /**
     * UC-5.7: Sinh viên xem điểm chi tiết
     */
    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<EvaluationDto>>> getMyEvaluations(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<EvaluationDto> evaluations = evaluationService.getEvaluationsByStudent(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(evaluations));
    }
    
    /**
     * UC-5.7: Sinh viên xem điểm tổng kết
     */
    @GetMapping("/student/final-scores")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<FinalScoreDto>>> getMyFinalScores(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<FinalScoreDto> scores = evaluationService.getFinalScoresByStudent(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(scores));
    }
    
    /**
     * Lấy các đánh giá của đề tài
     */
    @GetMapping("/topic/{topicId}/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINING_DEPT', 'DEPT_HEAD', 'LECTURER')")
    public ResponseEntity<ApiResponse<List<EvaluationDto>>> getTopicEvaluations(
            @PathVariable Long topicId,
            @PathVariable Long studentId) {
        List<EvaluationDto> evaluations = evaluationService.getEvaluationsByTopic(topicId, studentId);
        return ResponseEntity.ok(ApiResponse.success(evaluations));
    }
    
    /**
     * Lấy điểm tổng kết theo học kỳ
     */
    @GetMapping("/final-scores/semester/{semester}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINING_DEPT', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<List<FinalScoreDto>>> getFinalScoresBySemester(
            @PathVariable String semester) {
        List<FinalScoreDto> scores = evaluationService.getFinalScoresBySemester(semester);
        return ResponseEntity.ok(ApiResponse.success(scores));
    }
}

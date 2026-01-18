package com.university.gradpro.progress.service;

import com.university.gradpro.common.constant.ProgressStatus;
import com.university.gradpro.common.constant.RoleType;
import com.university.gradpro.common.exception.BadRequestException;
import com.university.gradpro.common.exception.ForbiddenException;
import com.university.gradpro.common.exception.ResourceNotFoundException;
import com.university.gradpro.common.response.PageResponse;
import com.university.gradpro.common.util.FileUtil;
import com.university.gradpro.progress.dto.*;
import com.university.gradpro.progress.entity.Milestone;
import com.university.gradpro.progress.entity.ProgressReport;
import com.university.gradpro.progress.repository.MilestoneRepository;
import com.university.gradpro.progress.repository.ProgressReportRepository;
import com.university.gradpro.user.entity.User;
import com.university.gradpro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressReportService {
    
    private final ProgressReportRepository progressReportRepository;
    private final MilestoneRepository milestoneRepository;
    private final UserRepository userRepository;
    private final FileUtil fileUtil;
    
    /**
     * UC-5.4, UC-5.5: Sinh viên cập nhật tiến độ / nộp báo cáo giai đoạn
     */
    @Transactional
    public ProgressReportDto submitProgress(Long studentId, SubmitProgressRequest request, MultipartFile file) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "id", studentId));
        
        if (student.getRole() != RoleType.STUDENT) {
            throw new ForbiddenException("Chỉ sinh viên mới có thể nộp báo cáo");
        }
        
        Milestone milestone = milestoneRepository.findById(request.getMilestoneId())
                .orElseThrow(() -> new ResourceNotFoundException("Mốc thời gian", "id", request.getMilestoneId()));
        
        // Kiểm tra hoặc tạo progress report
        ProgressReport progressReport = progressReportRepository
                .findByMilestoneIdAndStudentId(milestone.getId(), studentId)
                .orElse(ProgressReport.builder()
                        .milestone(milestone)
                        .student(student)
                        .status(ProgressStatus.NOT_STARTED)
                        .build());
        
        progressReport.setContent(request.getContent());
        progressReport.setStatus(ProgressStatus.SUBMITTED);
        progressReport.setSubmittedAt(LocalDateTime.now());
        
        // Xử lý file upload
        if (file != null && !file.isEmpty()) {
            String filePath = fileUtil.saveFile(file, "progress/" + milestone.getId());
            progressReport.setFilePath(filePath);
            progressReport.setFileName(file.getOriginalFilename());
        }
        
        progressReport = progressReportRepository.save(progressReport);
        return toDto(progressReport);
    }
    
    /**
     * UC-4.5: Giảng viên đánh giá tiến độ
     */
    @Transactional
    public ProgressReportDto evaluateProgress(Long progressId, Long lecturerId, EvaluateProgressRequest request) {
        ProgressReport progressReport = progressReportRepository.findById(progressId)
                .orElseThrow(() -> new ResourceNotFoundException("Báo cáo tiến độ", "id", progressId));
        
        // Kiểm tra quyền
        Milestone milestone = progressReport.getMilestone();
        var topic = milestone.getTopic();
        boolean isSupervisor = topic.getSupervisor() != null && topic.getSupervisor().getId().equals(lecturerId);
        
        if (!isSupervisor) {
            throw new ForbiddenException("Bạn không có quyền đánh giá báo cáo này");
        }
        
        if (progressReport.getStatus() != ProgressStatus.SUBMITTED) {
            throw new BadRequestException("Báo cáo chưa được nộp hoặc đã được đánh giá");
        }
        
        progressReport.setScore(request.getScore());
        progressReport.setLecturerComment(request.getComment());
        progressReport.setStatus(request.isApproved() ? ProgressStatus.APPROVED : ProgressStatus.REJECTED);
        progressReport.setReviewedAt(LocalDateTime.now());
        
        progressReport = progressReportRepository.save(progressReport);
        return toDto(progressReport);
    }
    
    /**
     * Lấy báo cáo tiến độ của sinh viên
     */
    public List<ProgressReportDto> getProgressByStudent(Long studentId) {
        return progressReportRepository.findByStudentId(studentId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy báo cáo theo đề tài
     */
    public List<ProgressReportDto> getProgressByTopic(Long topicId) {
        return progressReportRepository.findByTopicId(topicId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy báo cáo chờ đánh giá của GVHD
     */
    public PageResponse<ProgressReportDto> getPendingProgressBySupervisor(Long supervisorId, Pageable pageable) {
        Page<ProgressReport> page = progressReportRepository.findBySupervisorIdAndStatus(
                supervisorId, ProgressStatus.SUBMITTED, pageable);
        return PageResponse.from(page.map(this::toDto));
    }
    
    public ProgressReportDto getProgressById(Long id) {
        ProgressReport progressReport = progressReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Báo cáo tiến độ", "id", id));
        return toDto(progressReport);
    }
    
    private ProgressReportDto toDto(ProgressReport report) {
        return ProgressReportDto.builder()
                .id(report.getId())
                .status(report.getStatus())
                .content(report.getContent())
                .filePath(report.getFilePath())
                .fileName(report.getFileName())
                .lecturerComment(report.getLecturerComment())
                .score(report.getScore())
                .submittedAt(report.getSubmittedAt())
                .reviewedAt(report.getReviewedAt())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .milestone(ProgressReportDto.MilestoneInfo.builder()
                        .id(report.getMilestone().getId())
                        .title(report.getMilestone().getTitle())
                        .dueDate(report.getMilestone().getDueDate())
                        .orderIndex(report.getMilestone().getOrderIndex())
                        .build())
                .student(ProgressReportDto.StudentInfo.builder()
                        .id(report.getStudent().getId())
                        .code(report.getStudent().getCode())
                        .fullName(report.getStudent().getFullName())
                        .build())
                .build();
    }
}

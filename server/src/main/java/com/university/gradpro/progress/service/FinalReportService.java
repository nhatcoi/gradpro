package com.university.gradpro.progress.service;

import com.university.gradpro.common.constant.ProgressStatus;
import com.university.gradpro.common.constant.RoleType;
import com.university.gradpro.common.constant.TopicStatus;
import com.university.gradpro.common.exception.BadRequestException;
import com.university.gradpro.common.exception.ForbiddenException;
import com.university.gradpro.common.exception.ResourceNotFoundException;
import com.university.gradpro.common.util.FileUtil;
import com.university.gradpro.progress.dto.FinalReportDto;
import com.university.gradpro.progress.dto.SubmitFinalReportRequest;
import com.university.gradpro.progress.entity.FinalReport;
import com.university.gradpro.progress.repository.FinalReportRepository;
import com.university.gradpro.topic.entity.Topic;
import com.university.gradpro.topic.repository.TopicRepository;
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
public class FinalReportService {
    
    private final FinalReportRepository finalReportRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final FileUtil fileUtil;
    
    /**
     * UC-5.6: Sinh viên nộp báo cáo cuối kỳ
     */
    @Transactional
    public FinalReportDto submitFinalReport(Long studentId, SubmitFinalReportRequest request, MultipartFile file) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "id", studentId));
        
        if (student.getRole() != RoleType.STUDENT) {
            throw new ForbiddenException("Chỉ sinh viên mới có thể nộp báo cáo");
        }
        
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Đề tài", "id", request.getTopicId()));
        
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Vui lòng đính kèm file báo cáo");
        }
        
        // Kiểm tra hoặc tạo final report
        FinalReport finalReport = finalReportRepository
                .findByTopicIdAndStudentId(topic.getId(), studentId)
                .orElse(FinalReport.builder()
                        .topic(topic)
                        .student(student)
                        .build());
        
        // Upload file
        String filePath = fileUtil.saveFile(file, "final-reports/" + topic.getId());
        finalReport.setFilePath(filePath);
        finalReport.setFileName(file.getOriginalFilename());
        finalReport.setAbstractContent(request.getAbstractContent());
        finalReport.setStatus(ProgressStatus.SUBMITTED);
        finalReport.setSubmittedAt(LocalDateTime.now());
        
        finalReport = finalReportRepository.save(finalReport);
        
        // Cập nhật trạng thái đề tài
        topic.setStatus(TopicStatus.COMPLETED);
        topicRepository.save(topic);
        
        return toDto(finalReport);
    }
    
    /**
     * Đánh dấu đủ điều kiện bảo vệ
     */
    @Transactional
    public FinalReportDto markEligibleForDefense(Long finalReportId, Long lecturerId, boolean eligible) {
        FinalReport finalReport = finalReportRepository.findById(finalReportId)
                .orElseThrow(() -> new ResourceNotFoundException("Báo cáo cuối", "id", finalReportId));
        
        Topic topic = finalReport.getTopic();
        boolean isSupervisor = topic.getSupervisor() != null && topic.getSupervisor().getId().equals(lecturerId);
        
        if (!isSupervisor) {
            throw new ForbiddenException("Bạn không có quyền xác nhận báo cáo này");
        }
        
        finalReport.setEligibleForDefense(eligible);
        finalReport.setStatus(eligible ? ProgressStatus.APPROVED : ProgressStatus.REJECTED);
        
        finalReport = finalReportRepository.save(finalReport);
        return toDto(finalReport);
    }
    
    /**
     * Lấy báo cáo cuối của sinh viên
     */
    public List<FinalReportDto> getFinalReportsByStudent(Long studentId) {
        return finalReportRepository.findByStudentId(studentId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy báo cáo cuối theo GVHD
     */
    public Page<FinalReportDto> getFinalReportsBySupervisor(Long supervisorId, Pageable pageable) {
        return finalReportRepository.findBySupervisorId(supervisorId, pageable)
                .map(this::toDto);
    }
    
    /**
     * Lấy các báo cáo đủ điều kiện bảo vệ
     */
    public List<FinalReportDto> getEligibleReports(String semester) {
        return finalReportRepository.findEligibleBySemester(semester)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public FinalReportDto getFinalReportById(Long id) {
        FinalReport finalReport = finalReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Báo cáo cuối", "id", id));
        return toDto(finalReport);
    }
    
    private FinalReportDto toDto(FinalReport report) {
        return FinalReportDto.builder()
                .id(report.getId())
                .filePath(report.getFilePath())
                .fileName(report.getFileName())
                .abstractContent(report.getAbstractContent())
                .status(report.getStatus())
                .eligibleForDefense(report.getEligibleForDefense())
                .submittedAt(report.getSubmittedAt())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .topic(FinalReportDto.TopicInfo.builder()
                        .id(report.getTopic().getId())
                        .code(report.getTopic().getCode())
                        .title(report.getTopic().getTitle())
                        .build())
                .student(FinalReportDto.StudentInfo.builder()
                        .id(report.getStudent().getId())
                        .code(report.getStudent().getCode())
                        .fullName(report.getStudent().getFullName())
                        .build())
                .build();
    }
}

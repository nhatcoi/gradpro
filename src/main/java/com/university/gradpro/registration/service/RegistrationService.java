package com.university.gradpro.registration.service;

import com.university.gradpro.common.constant.RegistrationStatus;
import com.university.gradpro.common.constant.RoleType;
import com.university.gradpro.common.constant.TopicStatus;
import com.university.gradpro.common.exception.BadRequestException;
import com.university.gradpro.common.exception.ForbiddenException;
import com.university.gradpro.common.exception.ResourceNotFoundException;
import com.university.gradpro.common.response.PageResponse;
import com.university.gradpro.registration.dto.*;
import com.university.gradpro.registration.entity.Registration;
import com.university.gradpro.registration.entity.RegistrationPeriod;
import com.university.gradpro.registration.repository.RegistrationPeriodRepository;
import com.university.gradpro.registration.repository.RegistrationRepository;
import com.university.gradpro.topic.entity.Topic;
import com.university.gradpro.topic.repository.TopicRepository;
import com.university.gradpro.user.entity.User;
import com.university.gradpro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    
    private final RegistrationRepository registrationRepository;
    private final RegistrationPeriodRepository periodRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    
    /**
     * UC-5.3: Sinh viên đăng ký đề tài
     */
    @Transactional
    public RegistrationDto registerTopic(Long studentId, CreateRegistrationRequest request) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "id", studentId));
        
        if (student.getRole() != RoleType.STUDENT) {
            throw new ForbiddenException("Chỉ sinh viên mới có thể đăng ký đề tài");
        }
        
        // Kiểm tra đợt đăng ký hiện tại
        RegistrationPeriod currentPeriod = periodRepository
                .findCurrentOpenPeriod(LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Hiện tại không có đợt đăng ký nào đang mở"));
        
        // Kiểm tra sinh viên đã đăng ký trong đợt này chưa
        if (registrationRepository.existsByStudentIdAndPeriodId(studentId, currentPeriod.getId())) {
            throw new BadRequestException("Bạn đã đăng ký đề tài trong đợt này rồi");
        }
        
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Đề tài", "id", request.getTopicId()));
        
        if (topic.getStatus() != TopicStatus.APPROVED) {
            throw new BadRequestException("Đề tài chưa được phê duyệt");
        }
        
        // Kiểm tra số lượng sinh viên đã đăng ký
        long approvedCount = registrationRepository.countApprovedByTopicId(topic.getId());
        if (approvedCount >= topic.getMaxStudents()) {
            throw new BadRequestException("Đề tài đã đủ số lượng sinh viên");
        }
        
        Registration registration = Registration.builder()
                .student(student)
                .topic(topic)
                .period(currentPeriod)
                .status(RegistrationStatus.PENDING)
                .note(request.getNote())
                .build();
        
        registration = registrationRepository.save(registration);
        return toDto(registration);
    }
    
    /**
     * UC-4.3: Giảng viên chọn sinh viên (phê duyệt đăng ký)
     */
    @Transactional
    public RegistrationDto approveRegistration(Long registrationId, Long approverId, ApproveRegistrationRequest request) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Đăng ký", "id", registrationId));
        
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Người duyệt", "id", approverId));
        
        // Kiểm tra quyền duyệt
        Topic topic = registration.getTopic();
        boolean isLecturerOfTopic = topic.getLecturer() != null && topic.getLecturer().getId().equals(approverId);
        boolean isSupervisor = topic.getSupervisor() != null && topic.getSupervisor().getId().equals(approverId);
        
        if (!isLecturerOfTopic && !isSupervisor) {
            throw new ForbiddenException("Bạn không có quyền duyệt đăng ký này");
        }
        
        if (registration.getStatus() != RegistrationStatus.PENDING) {
            throw new BadRequestException("Đăng ký này đã được xử lý");
        }
        
        if (request.isApproved()) {
            // Kiểm tra số lượng sinh viên
            long approvedCount = registrationRepository.countApprovedByTopicId(topic.getId());
            if (approvedCount >= topic.getMaxStudents()) {
                throw new BadRequestException("Đề tài đã đủ số lượng sinh viên");
            }
            
            registration.setStatus(RegistrationStatus.APPROVED);
            registration.setApprovedBy(approver);
            registration.setApprovedAt(LocalDateTime.now());
            
            // Cập nhật trạng thái đề tài
            topic.setStatus(TopicStatus.REGISTERED);
            topicRepository.save(topic);
        } else {
            if (request.getRejectionReason() == null || request.getRejectionReason().isBlank()) {
                throw new BadRequestException("Vui lòng nhập lý do từ chối");
            }
            registration.setStatus(RegistrationStatus.REJECTED);
            registration.setRejectionReason(request.getRejectionReason());
        }
        
        registration = registrationRepository.save(registration);
        return toDto(registration);
    }
    
    /**
     * Hủy đăng ký (sinh viên)
     */
    @Transactional
    public void cancelRegistration(Long registrationId, Long studentId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Đăng ký", "id", registrationId));
        
        if (!registration.getStudent().getId().equals(studentId)) {
            throw new ForbiddenException("Bạn không có quyền hủy đăng ký này");
        }
        
        if (registration.getStatus() != RegistrationStatus.PENDING) {
            throw new BadRequestException("Không thể hủy đăng ký đã được xử lý");
        }
        
        registration.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
    }
    
    /**
     * Lấy đăng ký của sinh viên
     */
    public List<RegistrationDto> getRegistrationsByStudent(Long studentId) {
        return registrationRepository.findByStudentId(studentId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy đăng ký theo đề tài của giảng viên
     */
    public PageResponse<RegistrationDto> getRegistrationsByLecturer(Long lecturerId, RegistrationStatus status, Pageable pageable) {
        Page<Registration> page;
        if (status != null) {
            page = registrationRepository.findByLecturerIdAndStatus(lecturerId, status, pageable);
        } else {
            page = registrationRepository.findByLecturerId(lecturerId, pageable);
        }
        return PageResponse.from(page.map(this::toDto));
    }
    
    /**
     * Lấy tất cả đăng ký theo trạng thái
     */
    public PageResponse<RegistrationDto> getRegistrationsByStatus(RegistrationStatus status, Pageable pageable) {
        Page<Registration> page = registrationRepository.findByStatus(status, pageable);
        return PageResponse.from(page.map(this::toDto));
    }
    
    public RegistrationDto getRegistrationById(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đăng ký", "id", id));
        return toDto(registration);
    }
    
    private RegistrationDto toDto(Registration registration) {
        RegistrationDto.RegistrationDtoBuilder builder = RegistrationDto.builder()
                .id(registration.getId())
                .status(registration.getStatus())
                .note(registration.getNote())
                .rejectionReason(registration.getRejectionReason())
                .approvedAt(registration.getApprovedAt())
                .createdAt(registration.getCreatedAt())
                .updatedAt(registration.getUpdatedAt());
        
        User student = registration.getStudent();
        builder.student(RegistrationDto.StudentInfo.builder()
                .id(student.getId())
                .code(student.getCode())
                .fullName(student.getFullName())
                .email(student.getEmail())
                .major(student.getMajor())
                .academicYear(student.getAcademicYear())
                .build());
        
        Topic topic = registration.getTopic();
        String lecturerName = topic.getLecturer() != null ? topic.getLecturer().getFullName() : 
                              (topic.getSupervisor() != null ? topic.getSupervisor().getFullName() : "");
        builder.topic(RegistrationDto.TopicInfo.builder()
                .id(topic.getId())
                .code(topic.getCode())
                .title(topic.getTitle())
                .lecturerName(lecturerName)
                .build());
        
        RegistrationPeriod period = registration.getPeriod();
        builder.period(RegistrationDto.PeriodInfo.builder()
                .id(period.getId())
                .name(period.getName())
                .semester(period.getSemester())
                .build());
        
        if (registration.getApprovedBy() != null) {
            builder.approvedBy(RegistrationDto.ApproverInfo.builder()
                    .id(registration.getApprovedBy().getId())
                    .fullName(registration.getApprovedBy().getFullName())
                    .build());
        }
        
        return builder.build();
    }
}

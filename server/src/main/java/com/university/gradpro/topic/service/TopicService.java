package com.university.gradpro.topic.service;

import com.university.gradpro.common.constant.RoleType;
import com.university.gradpro.common.constant.TopicStatus;
import com.university.gradpro.common.exception.BadRequestException;
import com.university.gradpro.common.exception.ForbiddenException;
import com.university.gradpro.common.exception.ResourceNotFoundException;
import com.university.gradpro.common.response.PageResponse;
import com.university.gradpro.topic.dto.*;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {
    
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    
    /**
     * UC-4.1: Giảng viên đăng ký đề tài mở
     */
    @Transactional
    public TopicDto createTopicByLecturer(Long lecturerId, CreateTopicRequest request) {
        User lecturer = userRepository.findById(lecturerId)
                .orElseThrow(() -> new ResourceNotFoundException("Giảng viên", "id", lecturerId));
        
        if (lecturer.getRole() != RoleType.LECTURER && lecturer.getRole() != RoleType.DEPT_HEAD) {
            throw new ForbiddenException("Chỉ giảng viên mới có thể đăng ký đề tài");
        }
        
        String code = generateTopicCode(request.getSemester());
        
        Topic topic = Topic.builder()
                .code(code)
                .title(request.getTitle())
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .expectedOutcome(request.getExpectedOutcome())
                .technology(request.getTechnology())
                .lecturer(lecturer)
                .supervisor(lecturer)
                .department(request.getDepartment() != null ? request.getDepartment() : lecturer.getDepartment())
                .major(request.getMajor() != null ? request.getMajor() : lecturer.getMajor())
                .semester(request.getSemester())
                .maxStudents(request.getMaxStudents() != null ? request.getMaxStudents() : 1)
                .status(TopicStatus.PENDING)
                .build();
        
        topic = topicRepository.save(topic);
        return toDto(topic);
    }
    
    /**
     * UC-5.1: Sinh viên đề xuất đề tài
     */
    @Transactional
    public TopicDto createTopicByStudent(Long studentId, StudentProposalRequest request) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "id", studentId));
        
        if (student.getRole() != RoleType.STUDENT) {
            throw new ForbiddenException("Chỉ sinh viên mới có thể đề xuất đề tài");
        }
        
        User supervisor = userRepository.findById(request.getSupervisorId())
                .orElseThrow(() -> new ResourceNotFoundException("Giảng viên", "id", request.getSupervisorId()));
        
        if (supervisor.getRole() != RoleType.LECTURER && supervisor.getRole() != RoleType.DEPT_HEAD) {
            throw new BadRequestException("Người được chọn không phải giảng viên");
        }
        
        // Lấy học kỳ hiện tại (đơn giản hóa)
        String currentSemester = getCurrentSemester();
        String code = generateTopicCode(currentSemester);
        
        Topic topic = Topic.builder()
                .code(code)
                .title(request.getTitle())
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .expectedOutcome(request.getExpectedOutcome())
                .technology(request.getTechnology())
                .student(student)
                .supervisor(supervisor)
                .department(student.getDepartment())
                .major(student.getMajor())
                .semester(currentSemester)
                .maxStudents(1)
                .status(TopicStatus.PENDING)
                .build();
        
        topic = topicRepository.save(topic);
        return toDto(topic);
    }
    
    /**
     * UC-3.1: Trưởng BM phê duyệt đề tài
     */
    @Transactional
    public TopicDto approveTopic(Long topicId, Long approverId, ApproveTopicRequest request) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Đề tài", "id", topicId));
        
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("Người duyệt", "id", approverId));
        
        if (topic.getStatus() != TopicStatus.PENDING) {
            throw new BadRequestException("Chỉ có thể phê duyệt đề tài đang chờ duyệt");
        }
        
        if (request.isApproved()) {
            topic.setStatus(TopicStatus.APPROVED);
            topic.setApprovedBy(approver);
            topic.setApprovedAt(LocalDateTime.now());
        } else {
            if (request.getRejectionReason() == null || request.getRejectionReason().isBlank()) {
                throw new BadRequestException("Vui lòng nhập lý do từ chối");
            }
            topic.setStatus(TopicStatus.REJECTED);
            topic.setRejectionReason(request.getRejectionReason());
        }
        
        topic = topicRepository.save(topic);
        return toDto(topic);
    }
    
    /**
     * UC-3.2: Phân công GVHD
     */
    @Transactional
    public TopicDto assignSupervisor(Long topicId, Long supervisorId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Đề tài", "id", topicId));
        
        User supervisor = userRepository.findById(supervisorId)
                .orElseThrow(() -> new ResourceNotFoundException("Giảng viên", "id", supervisorId));
        
        if (supervisor.getRole() != RoleType.LECTURER && supervisor.getRole() != RoleType.DEPT_HEAD) {
            throw new BadRequestException("Người được chọn không phải giảng viên");
        }
        
        topic.setSupervisor(supervisor);
        topic = topicRepository.save(topic);
        return toDto(topic);
    }
    
    /**
     * Cập nhật đề tài
     */
    @Transactional
    public TopicDto updateTopic(Long topicId, Long userId, UpdateTopicRequest request) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Đề tài", "id", topicId));
        
        // Kiểm tra quyền sửa
        boolean isOwner = (topic.getLecturer() != null && topic.getLecturer().getId().equals(userId)) ||
                          (topic.getStudent() != null && topic.getStudent().getId().equals(userId));
        
        if (!isOwner) {
            throw new ForbiddenException("Bạn không có quyền sửa đề tài này");
        }
        
        if (topic.getStatus() != TopicStatus.PENDING && topic.getStatus() != TopicStatus.REJECTED) {
            throw new BadRequestException("Không thể sửa đề tài đã được duyệt");
        }
        
        if (request.getTitle() != null) {
            topic.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            topic.setDescription(request.getDescription());
        }
        if (request.getRequirements() != null) {
            topic.setRequirements(request.getRequirements());
        }
        if (request.getExpectedOutcome() != null) {
            topic.setExpectedOutcome(request.getExpectedOutcome());
        }
        if (request.getTechnology() != null) {
            topic.setTechnology(request.getTechnology());
        }
        if (request.getMaxStudents() != null) {
            topic.setMaxStudents(request.getMaxStudents());
        }
        
        // Nếu bị từ chối, chuyển lại về chờ duyệt khi sửa
        if (topic.getStatus() == TopicStatus.REJECTED) {
            topic.setStatus(TopicStatus.PENDING);
            topic.setRejectionReason(null);
        }
        
        topic = topicRepository.save(topic);
        return toDto(topic);
    }
    
    /**
     * UC-4.2, UC-5.2: Tìm kiếm đề tài
     */
    @Transactional(readOnly = true)
    public PageResponse<TopicDto> searchTopics(String keyword, Pageable pageable) {
        Page<Topic> page = topicRepository.searchTopics(keyword, pageable);
        return PageResponse.from(page.map(this::toDto));
    }
    
    /**
     * Lấy đề tài có sẵn để đăng ký
     */
    @Transactional(readOnly = true)
    public PageResponse<TopicDto> getAvailableTopics(String semester, Pageable pageable) {
        Page<Topic> page;
        if (semester != null && !semester.isBlank()) {
            page = topicRepository.findAvailableTopicsBySemester(semester, pageable);
        } else {
            page = topicRepository.findAvailableTopics(pageable);
        }
        return PageResponse.from(page.map(this::toDto));
    }
    
    /**
     * Lấy đề tài theo trạng thái
     */
    @Transactional(readOnly = true)
    public PageResponse<TopicDto> getTopicsByStatus(TopicStatus status, Pageable pageable) {
        Page<Topic> page = topicRepository.findByStatus(status, pageable);
        return PageResponse.from(page.map(this::toDto));
    }
    
    /**
     * Lấy đề tài của giảng viên
     */
    @Transactional(readOnly = true)
    public List<TopicDto> getTopicsByLecturer(Long lecturerId) {
        return topicRepository.findByLecturerId(lecturerId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy đề tài mà giảng viên đang hướng dẫn
     */
    @Transactional(readOnly = true)
    public List<TopicDto> getTopicsBySupervisor(Long supervisorId) {
        return topicRepository.findBySupervisorId(supervisorId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public TopicDto getTopicById(Long id) {
        Topic topic = topicRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đề tài", "id", id));
        return toDto(topic);
    }
    
    @Transactional(readOnly = true)
    public TopicDto getTopicByCode(String code) {
        Topic topic = topicRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Đề tài", "mã", code));
        return toDto(topic);
    }
    
    private String generateTopicCode(String semester) {
        String prefix = "DT-" + semester.replace("-", "").substring(0, 6) + "-";
        return prefix + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
    
    private String getCurrentSemester() {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        
        if (month >= 9) {
            return "HK1-" + year + "-" + (year + 1);
        } else if (month >= 2) {
            return "HK2-" + (year - 1) + "-" + year;
        } else {
            return "HK1-" + (year - 1) + "-" + year;
        }
    }
    
    private TopicDto toDto(Topic topic) {
        TopicDto.TopicDtoBuilder builder = TopicDto.builder()
                .id(topic.getId())
                .code(topic.getCode())
                .title(topic.getTitle())
                .description(topic.getDescription())
                .requirements(topic.getRequirements())
                .expectedOutcome(topic.getExpectedOutcome())
                .technology(topic.getTechnology())
                .status(topic.getStatus())
                .department(topic.getDepartment())
                .major(topic.getMajor())
                .semester(topic.getSemester())
                .maxStudents(topic.getMaxStudents())
                .rejectionReason(topic.getRejectionReason())
                .approvedAt(topic.getApprovedAt())
                .createdAt(topic.getCreatedAt())
                .updatedAt(topic.getUpdatedAt());
        
        if (topic.getLecturer() != null) {
            builder.lecturer(toLecturerInfo(topic.getLecturer()));
        }
        if (topic.getSupervisor() != null) {
            builder.supervisor(toLecturerInfo(topic.getSupervisor()));
        }
        if (topic.getStudent() != null) {
            builder.student(toStudentInfo(topic.getStudent()));
        }
        if (topic.getApprovedBy() != null) {
            builder.approvedBy(toLecturerInfo(topic.getApprovedBy()));
        }
        
        return builder.build();
    }
    
    private TopicDto.LecturerInfo toLecturerInfo(User user) {
        return TopicDto.LecturerInfo.builder()
                .id(user.getId())
                .code(user.getCode())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .academicTitle(user.getAcademicTitle())
                .department(user.getDepartment())
                .build();
    }
    
    private TopicDto.StudentInfo toStudentInfo(User user) {
        return TopicDto.StudentInfo.builder()
                .id(user.getId())
                .code(user.getCode())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .major(user.getMajor())
                .academicYear(user.getAcademicYear())
                .build();
    }
}

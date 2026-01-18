package com.university.gradpro.council.service;

import com.university.gradpro.common.constant.CouncilRole;
import com.university.gradpro.common.constant.RoleType;
import com.university.gradpro.common.exception.BadRequestException;
import com.university.gradpro.common.exception.DuplicateResourceException;
import com.university.gradpro.common.exception.ResourceNotFoundException;
import com.university.gradpro.council.dto.*;
import com.university.gradpro.council.entity.Council;
import com.university.gradpro.council.entity.CouncilMember;
import com.university.gradpro.council.entity.DefenseSchedule;
import com.university.gradpro.council.repository.CouncilMemberRepository;
import com.university.gradpro.council.repository.CouncilRepository;
import com.university.gradpro.council.repository.DefenseScheduleRepository;
import com.university.gradpro.topic.entity.Topic;
import com.university.gradpro.topic.repository.TopicRepository;
import com.university.gradpro.user.entity.User;
import com.university.gradpro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouncilService {
    
    private final CouncilRepository councilRepository;
    private final CouncilMemberRepository councilMemberRepository;
    private final DefenseScheduleRepository defenseScheduleRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    
    /**
     * UC-3.3: Tạo hội đồng
     */
    @Transactional
    public CouncilDto createCouncil(CreateCouncilRequest request) {
        String code = generateCouncilCode(request.getSemester());
        
        Council council = Council.builder()
                .code(code)
                .name(request.getName())
                .department(request.getDepartment())
                .semester(request.getSemester())
                .defenseDate(request.getDefenseDate())
                .room(request.getRoom())
                .notes(request.getNotes())
                .active(true)
                .members(new ArrayList<>())
                .schedules(new ArrayList<>())
                .build();
        
        council = councilRepository.save(council);
        
        // Thêm thành viên hội đồng
        if (request.getMembers() != null && !request.getMembers().isEmpty()) {
            for (CreateCouncilRequest.MemberRequest memberReq : request.getMembers()) {
                addMember(council, memberReq.getLecturerId(), memberReq.getRole());
            }
        }
        
        council = councilRepository.findById(council.getId()).orElse(council);
        return toDto(council);
    }
    
    /**
     * Cập nhật hội đồng
     */
    @Transactional
    public CouncilDto updateCouncil(Long id, CreateCouncilRequest request) {
        Council council = councilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hội đồng", "id", id));
        
        council.setName(request.getName());
        council.setDepartment(request.getDepartment());
        council.setSemester(request.getSemester());
        council.setDefenseDate(request.getDefenseDate());
        council.setRoom(request.getRoom());
        council.setNotes(request.getNotes());
        
        council = councilRepository.save(council);
        return toDto(council);
    }
    
    /**
     * Thêm thành viên hội đồng
     */
    @Transactional
    public CouncilDto addCouncilMember(Long councilId, Long lecturerId, String role) {
        Council council = councilRepository.findById(councilId)
                .orElseThrow(() -> new ResourceNotFoundException("Hội đồng", "id", councilId));
        
        addMember(council, lecturerId, role);
        
        council = councilRepository.findById(councilId).orElse(council);
        return toDto(council);
    }
    
    /**
     * Xóa thành viên hội đồng
     */
    @Transactional
    public CouncilDto removeCouncilMember(Long councilId, Long lecturerId) {
        Council council = councilRepository.findById(councilId)
                .orElseThrow(() -> new ResourceNotFoundException("Hội đồng", "id", councilId));
        
        CouncilMember member = councilMemberRepository.findByCouncilIdAndLecturerId(councilId, lecturerId)
                .orElseThrow(() -> new ResourceNotFoundException("Thành viên", "lecturerId", lecturerId));
        
        councilMemberRepository.delete(member);
        
        council = councilRepository.findById(councilId).orElse(council);
        return toDto(council);
    }
    
    /**
     * UC-2.3: Xếp lịch - Thêm lịch bảo vệ
     */
    @Transactional
    public CouncilDto addDefenseSchedule(Long councilId, AddScheduleRequest request) {
        Council council = councilRepository.findById(councilId)
                .orElseThrow(() -> new ResourceNotFoundException("Hội đồng", "id", councilId));
        
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Đề tài", "id", request.getTopicId()));
        
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Sinh viên", "id", request.getStudentId()));
        
        // Kiểm tra xem đề tài đã có lịch chưa
        if (defenseScheduleRepository.findByTopicId(topic.getId()).isPresent()) {
            throw new DuplicateResourceException("Đề tài này đã có lịch bảo vệ");
        }
        
        DefenseSchedule schedule = DefenseSchedule.builder()
                .council(council)
                .topic(topic)
                .student(student)
                .orderNumber(request.getOrderNumber())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .notes(request.getNotes())
                .completed(false)
                .build();
        
        defenseScheduleRepository.save(schedule);
        
        council = councilRepository.findById(councilId).orElse(council);
        return toDto(council);
    }
    
    /**
     * UC-2.2: Phân công hội đồng - Gán nhiều đề tài vào hội đồng
     */
    @Transactional
    public CouncilDto assignTopicsToCouncil(AssignCouncilRequest request) {
        Council council = councilRepository.findById(request.getCouncilId())
                .orElseThrow(() -> new ResourceNotFoundException("Hội đồng", "id", request.getCouncilId()));
        
        int currentOrder = defenseScheduleRepository.countByCouncilId(council.getId());
        
        for (Long topicId : request.getTopicIds()) {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new ResourceNotFoundException("Đề tài", "id", topicId));
            
            // Bỏ qua nếu đề tài đã có lịch
            if (defenseScheduleRepository.findByTopicId(topicId).isPresent()) {
                continue;
            }
            
            // Lấy sinh viên của đề tài (giả sử 1 đề tài 1 sinh viên)
            // Trong thực tế cần lấy từ Registration
            User student = topic.getStudent();
            if (student == null) {
                continue;
            }
            
            currentOrder++;
            
            DefenseSchedule schedule = DefenseSchedule.builder()
                    .council(council)
                    .topic(topic)
                    .student(student)
                    .orderNumber(currentOrder)
                    .completed(false)
                    .build();
            
            defenseScheduleRepository.save(schedule);
        }
        
        council = councilRepository.findById(council.getId()).orElse(council);
        return toDto(council);
    }
    
    /**
     * Đánh dấu hoàn thành bảo vệ
     */
    @Transactional
    public void markDefenseCompleted(Long scheduleId) {
        DefenseSchedule schedule = defenseScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Lịch bảo vệ", "id", scheduleId));
        
        schedule.setCompleted(true);
        defenseScheduleRepository.save(schedule);
    }
    
    public CouncilDto getCouncilById(Long id) {
        Council council = councilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hội đồng", "id", id));
        return toDto(council);
    }
    
    public CouncilDto getCouncilByCode(String code) {
        Council council = councilRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Hội đồng", "mã", code));
        return toDto(council);
    }
    
    public List<CouncilDto> getCouncilsBySemester(String semester) {
        return councilRepository.findBySemester(semester)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public Page<CouncilDto> getActiveCouncilsBySemester(String semester, Pageable pageable) {
        return councilRepository.findBySemesterAndActiveTrue(semester, pageable)
                .map(this::toDto);
    }
    
    public List<CouncilDto> getCouncilsByLecturer(Long lecturerId) {
        return councilRepository.findByMemberLecturerId(lecturerId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    private void addMember(Council council, Long lecturerId, String roleStr) {
        User lecturer = userRepository.findById(lecturerId)
                .orElseThrow(() -> new ResourceNotFoundException("Giảng viên", "id", lecturerId));
        
        if (lecturer.getRole() != RoleType.LECTURER && lecturer.getRole() != RoleType.DEPT_HEAD) {
            throw new BadRequestException("Người được chọn không phải giảng viên");
        }
        
        if (councilMemberRepository.existsByCouncilIdAndLecturerId(council.getId(), lecturerId)) {
            throw new DuplicateResourceException("Giảng viên đã là thành viên hội đồng");
        }
        
        CouncilRole role = CouncilRole.valueOf(roleStr.toUpperCase());
        
        CouncilMember member = CouncilMember.builder()
                .council(council)
                .lecturer(lecturer)
                .role(role)
                .build();
        
        councilMemberRepository.save(member);
    }
    
    private String generateCouncilCode(String semester) {
        String prefix = "HD-" + semester.replace("-", "").substring(0, 6) + "-";
        return prefix + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
    
    private CouncilDto toDto(Council council) {
        List<CouncilDto.MemberInfo> members = council.getMembers().stream()
                .map(m -> CouncilDto.MemberInfo.builder()
                        .id(m.getId())
                        .lecturerId(m.getLecturer().getId())
                        .lecturerCode(m.getLecturer().getCode())
                        .lecturerName(m.getLecturer().getFullName())
                        .academicTitle(m.getLecturer().getAcademicTitle())
                        .role(m.getRole())
                        .build())
                .collect(Collectors.toList());
        
        List<CouncilDto.ScheduleInfo> schedules = council.getSchedules().stream()
                .map(s -> CouncilDto.ScheduleInfo.builder()
                        .id(s.getId())
                        .topicId(s.getTopic().getId())
                        .topicCode(s.getTopic().getCode())
                        .topicTitle(s.getTopic().getTitle())
                        .studentId(s.getStudent().getId())
                        .studentCode(s.getStudent().getCode())
                        .studentName(s.getStudent().getFullName())
                        .orderNumber(s.getOrderNumber())
                        .startTime(s.getStartTime() != null ? s.getStartTime().toString() : null)
                        .endTime(s.getEndTime() != null ? s.getEndTime().toString() : null)
                        .completed(s.getCompleted())
                        .build())
                .collect(Collectors.toList());
        
        return CouncilDto.builder()
                .id(council.getId())
                .code(council.getCode())
                .name(council.getName())
                .department(council.getDepartment())
                .semester(council.getSemester())
                .defenseDate(council.getDefenseDate())
                .room(council.getRoom())
                .notes(council.getNotes())
                .active(council.getActive())
                .createdAt(council.getCreatedAt())
                .updatedAt(council.getUpdatedAt())
                .members(members)
                .schedules(schedules)
                .build();
    }
}

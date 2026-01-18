package com.university.gradpro.progress.service;

import com.university.gradpro.common.exception.ForbiddenException;
import com.university.gradpro.common.exception.ResourceNotFoundException;
import com.university.gradpro.progress.dto.CreateMilestoneRequest;
import com.university.gradpro.progress.dto.MilestoneDto;
import com.university.gradpro.progress.entity.Milestone;
import com.university.gradpro.progress.repository.MilestoneRepository;
import com.university.gradpro.topic.entity.Topic;
import com.university.gradpro.topic.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MilestoneService {
    
    private final MilestoneRepository milestoneRepository;
    private final TopicRepository topicRepository;
    
    /**
     * UC-4.4: Thiết lập mốc thời gian
     */
    @Transactional
    public MilestoneDto createMilestone(Long lecturerId, CreateMilestoneRequest request) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Đề tài", "id", request.getTopicId()));
        
        // Kiểm tra quyền
        boolean isSupervisor = topic.getSupervisor() != null && topic.getSupervisor().getId().equals(lecturerId);
        boolean isLecturer = topic.getLecturer() != null && topic.getLecturer().getId().equals(lecturerId);
        
        if (!isSupervisor && !isLecturer) {
            throw new ForbiddenException("Bạn không có quyền thiết lập mốc cho đề tài này");
        }
        
        Milestone milestone = Milestone.builder()
                .topic(topic)
                .title(request.getTitle())
                .description(request.getDescription())
                .orderIndex(request.getOrderIndex())
                .dueDate(request.getDueDate())
                .weight(request.getWeight() != null ? request.getWeight() : 0.0)
                .build();
        
        milestone = milestoneRepository.save(milestone);
        return toDto(milestone);
    }
    
    /**
     * Cập nhật mốc thời gian
     */
    @Transactional
    public MilestoneDto updateMilestone(Long milestoneId, Long lecturerId, CreateMilestoneRequest request) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Mốc thời gian", "id", milestoneId));
        
        Topic topic = milestone.getTopic();
        boolean isSupervisor = topic.getSupervisor() != null && topic.getSupervisor().getId().equals(lecturerId);
        boolean isLecturer = topic.getLecturer() != null && topic.getLecturer().getId().equals(lecturerId);
        
        if (!isSupervisor && !isLecturer) {
            throw new ForbiddenException("Bạn không có quyền cập nhật mốc này");
        }
        
        milestone.setTitle(request.getTitle());
        milestone.setDescription(request.getDescription());
        milestone.setOrderIndex(request.getOrderIndex());
        milestone.setDueDate(request.getDueDate());
        if (request.getWeight() != null) {
            milestone.setWeight(request.getWeight());
        }
        
        milestone = milestoneRepository.save(milestone);
        return toDto(milestone);
    }
    
    /**
     * Xóa mốc thời gian
     */
    @Transactional
    public void deleteMilestone(Long milestoneId, Long lecturerId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Mốc thời gian", "id", milestoneId));
        
        Topic topic = milestone.getTopic();
        boolean isSupervisor = topic.getSupervisor() != null && topic.getSupervisor().getId().equals(lecturerId);
        boolean isLecturer = topic.getLecturer() != null && topic.getLecturer().getId().equals(lecturerId);
        
        if (!isSupervisor && !isLecturer) {
            throw new ForbiddenException("Bạn không có quyền xóa mốc này");
        }
        
        milestoneRepository.delete(milestone);
    }
    
    /**
     * Lấy các mốc của đề tài
     */
    @Transactional(readOnly = true)
    public List<MilestoneDto> getMilestonesByTopic(Long topicId) {
        return milestoneRepository.findByTopicIdOrderByOrderIndexAsc(topicId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public MilestoneDto getMilestoneById(Long id) {
        Milestone milestone = milestoneRepository.findByIdWithTopic(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mốc thời gian", "id", id));
        return toDto(milestone);
    }
    
    private MilestoneDto toDto(Milestone milestone) {
        return MilestoneDto.builder()
                .id(milestone.getId())
                .topicId(milestone.getTopic().getId())
                .topicTitle(milestone.getTopic().getTitle())
                .title(milestone.getTitle())
                .description(milestone.getDescription())
                .orderIndex(milestone.getOrderIndex())
                .dueDate(milestone.getDueDate())
                .weight(milestone.getWeight())
                .createdAt(milestone.getCreatedAt())
                .updatedAt(milestone.getUpdatedAt())
                .build();
    }
}

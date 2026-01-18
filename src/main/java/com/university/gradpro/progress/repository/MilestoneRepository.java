package com.university.gradpro.progress.repository;

import com.university.gradpro.progress.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    
    List<Milestone> findByTopicIdOrderByOrderIndexAsc(Long topicId);
    
    List<Milestone> findByTopicIdAndDueDateBefore(Long topicId, LocalDateTime date);
    
    List<Milestone> findByTopicIdAndDueDateAfter(Long topicId, LocalDateTime date);
    
    int countByTopicId(Long topicId);
    
    void deleteByTopicId(Long topicId);
}

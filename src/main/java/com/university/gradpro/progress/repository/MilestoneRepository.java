package com.university.gradpro.progress.repository;

import com.university.gradpro.progress.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    
    @Query("SELECT m FROM Milestone m LEFT JOIN FETCH m.topic WHERE m.topic.id = :topicId ORDER BY m.orderIndex ASC")
    List<Milestone> findByTopicIdOrderByOrderIndexAsc(@Param("topicId") Long topicId);
    
    List<Milestone> findByTopicIdAndDueDateBefore(Long topicId, LocalDateTime date);
    
    List<Milestone> findByTopicIdAndDueDateAfter(Long topicId, LocalDateTime date);
    
    int countByTopicId(Long topicId);
    
    void deleteByTopicId(Long topicId);
    
    @Query("SELECT m FROM Milestone m LEFT JOIN FETCH m.topic WHERE m.id = :id")
    Optional<Milestone> findByIdWithTopic(@Param("id") Long id);
}

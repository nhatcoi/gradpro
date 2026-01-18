package com.university.gradpro.progress.repository;

import com.university.gradpro.common.constant.ProgressStatus;
import com.university.gradpro.progress.entity.ProgressReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressReportRepository extends JpaRepository<ProgressReport, Long> {
    
    List<ProgressReport> findByStudentId(Long studentId);
    
    List<ProgressReport> findByMilestoneId(Long milestoneId);
    
    Optional<ProgressReport> findByMilestoneIdAndStudentId(Long milestoneId, Long studentId);
    
    List<ProgressReport> findByStudentIdAndStatus(Long studentId, ProgressStatus status);
    
    @Query("SELECT pr FROM ProgressReport pr WHERE pr.milestone.topic.supervisor.id = :supervisorId")
    Page<ProgressReport> findBySupervisorId(@Param("supervisorId") Long supervisorId, Pageable pageable);
    
    @Query("SELECT pr FROM ProgressReport pr WHERE pr.milestone.topic.supervisor.id = :supervisorId AND pr.status = :status")
    Page<ProgressReport> findBySupervisorIdAndStatus(@Param("supervisorId") Long supervisorId, 
                                                       @Param("status") ProgressStatus status, 
                                                       Pageable pageable);
    
    @Query("SELECT pr FROM ProgressReport pr WHERE pr.milestone.topic.id = :topicId ORDER BY pr.milestone.orderIndex")
    List<ProgressReport> findByTopicId(@Param("topicId") Long topicId);
    
    @Query("SELECT AVG(pr.score) FROM ProgressReport pr WHERE pr.student.id = :studentId AND pr.score IS NOT NULL")
    Double calculateAverageScoreByStudentId(@Param("studentId") Long studentId);
}

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
    
    @Query("SELECT pr FROM ProgressReport pr LEFT JOIN FETCH pr.milestone m LEFT JOIN FETCH m.topic t LEFT JOIN FETCH t.supervisor LEFT JOIN FETCH pr.student WHERE pr.student.id = :studentId")
    List<ProgressReport> findByStudentId(@Param("studentId") Long studentId);
    
    List<ProgressReport> findByMilestoneId(Long milestoneId);
    
    @Query("SELECT pr FROM ProgressReport pr LEFT JOIN FETCH pr.milestone m LEFT JOIN FETCH m.topic t LEFT JOIN FETCH t.supervisor LEFT JOIN FETCH pr.student WHERE m.id = :milestoneId AND pr.student.id = :studentId")
    Optional<ProgressReport> findByMilestoneIdAndStudentId(@Param("milestoneId") Long milestoneId, @Param("studentId") Long studentId);
    
    List<ProgressReport> findByStudentIdAndStatus(Long studentId, ProgressStatus status);
    
    @Query(value = "SELECT pr FROM ProgressReport pr LEFT JOIN FETCH pr.milestone m LEFT JOIN FETCH m.topic t LEFT JOIN FETCH t.supervisor LEFT JOIN FETCH pr.student WHERE t.supervisor.id = :supervisorId",
           countQuery = "SELECT COUNT(pr) FROM ProgressReport pr WHERE pr.milestone.topic.supervisor.id = :supervisorId")
    Page<ProgressReport> findBySupervisorId(@Param("supervisorId") Long supervisorId, Pageable pageable);
    
    @Query(value = "SELECT pr FROM ProgressReport pr LEFT JOIN FETCH pr.milestone m LEFT JOIN FETCH m.topic t LEFT JOIN FETCH t.supervisor LEFT JOIN FETCH pr.student WHERE t.supervisor.id = :supervisorId AND pr.status = :status",
           countQuery = "SELECT COUNT(pr) FROM ProgressReport pr WHERE pr.milestone.topic.supervisor.id = :supervisorId AND pr.status = :status")
    Page<ProgressReport> findBySupervisorIdAndStatus(@Param("supervisorId") Long supervisorId, 
                                                       @Param("status") ProgressStatus status, 
                                                       Pageable pageable);
    
    @Query("SELECT pr FROM ProgressReport pr LEFT JOIN FETCH pr.milestone m LEFT JOIN FETCH m.topic t LEFT JOIN FETCH t.supervisor LEFT JOIN FETCH pr.student WHERE t.id = :topicId ORDER BY m.orderIndex")
    List<ProgressReport> findByTopicId(@Param("topicId") Long topicId);
    
    @Query("SELECT pr FROM ProgressReport pr LEFT JOIN FETCH pr.milestone m LEFT JOIN FETCH m.topic t LEFT JOIN FETCH t.supervisor LEFT JOIN FETCH pr.student WHERE pr.id = :id")
    Optional<ProgressReport> findByIdWithRelations(@Param("id") Long id);
    
    @Query("SELECT AVG(pr.score) FROM ProgressReport pr WHERE pr.student.id = :studentId AND pr.score IS NOT NULL")
    Double calculateAverageScoreByStudentId(@Param("studentId") Long studentId);
}

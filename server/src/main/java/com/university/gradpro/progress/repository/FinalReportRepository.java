package com.university.gradpro.progress.repository;

import com.university.gradpro.common.constant.ProgressStatus;
import com.university.gradpro.progress.entity.FinalReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinalReportRepository extends JpaRepository<FinalReport, Long> {
    
    Optional<FinalReport> findByTopicIdAndStudentId(Long topicId, Long studentId);
    
    List<FinalReport> findByStudentId(Long studentId);
    
    List<FinalReport> findByTopicId(Long topicId);
    
    Page<FinalReport> findByStatus(ProgressStatus status, Pageable pageable);
    
    @Query("SELECT fr FROM FinalReport fr WHERE fr.topic.supervisor.id = :supervisorId")
    Page<FinalReport> findBySupervisorId(@Param("supervisorId") Long supervisorId, Pageable pageable);
    
    List<FinalReport> findByEligibleForDefenseTrue();
    
    @Query("SELECT fr FROM FinalReport fr WHERE fr.topic.semester = :semester AND fr.eligibleForDefense = true")
    List<FinalReport> findEligibleBySemester(@Param("semester") String semester);
}

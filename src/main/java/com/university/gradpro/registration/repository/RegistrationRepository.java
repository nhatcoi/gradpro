package com.university.gradpro.registration.repository;

import com.university.gradpro.common.constant.RegistrationStatus;
import com.university.gradpro.registration.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    
    List<Registration> findByStudentId(Long studentId);
    
    List<Registration> findByTopicId(Long topicId);
    
    Optional<Registration> findByStudentIdAndPeriodId(Long studentId, Long periodId);
    
    Optional<Registration> findByStudentIdAndTopicId(Long studentId, Long topicId);
    
    List<Registration> findByTopicIdAndStatus(Long topicId, RegistrationStatus status);
    
    Page<Registration> findByStatus(RegistrationStatus status, Pageable pageable);
    
    @Query("SELECT r FROM Registration r WHERE r.topic.lecturer.id = :lecturerId")
    Page<Registration> findByLecturerId(@Param("lecturerId") Long lecturerId, Pageable pageable);
    
    @Query("SELECT r FROM Registration r WHERE r.topic.lecturer.id = :lecturerId AND r.status = :status")
    Page<Registration> findByLecturerIdAndStatus(@Param("lecturerId") Long lecturerId, 
                                                   @Param("status") RegistrationStatus status, 
                                                   Pageable pageable);
    
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.topic.id = :topicId AND r.status = 'APPROVED'")
    long countApprovedByTopicId(@Param("topicId") Long topicId);
    
    @Query("SELECT COUNT(r) FROM Registration r WHERE r.period.id = :periodId AND r.status = :status")
    long countByPeriodIdAndStatus(@Param("periodId") Long periodId, @Param("status") RegistrationStatus status);
    
    boolean existsByStudentIdAndPeriodId(Long studentId, Long periodId);
}

package com.university.gradpro.topic.repository;

import com.university.gradpro.common.constant.TopicStatus;
import com.university.gradpro.topic.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    
    Optional<Topic> findByCode(String code);
    
    boolean existsByCode(String code);
    
    List<Topic> findByLecturerId(Long lecturerId);
    
    List<Topic> findBySupervisorId(Long supervisorId);
    
    Page<Topic> findByStatus(TopicStatus status, Pageable pageable);
    
    Page<Topic> findByLecturerIdAndStatus(Long lecturerId, TopicStatus status, Pageable pageable);
    
    @Query("SELECT t FROM Topic t WHERE t.status = 'APPROVED' AND " +
           "(SELECT COUNT(r) FROM Registration r WHERE r.topic = t AND r.status = 'APPROVED') < t.maxStudents")
    Page<Topic> findAvailableTopics(Pageable pageable);
    
    @Query("SELECT t FROM Topic t WHERE t.status = 'APPROVED' AND t.semester = :semester AND " +
           "(SELECT COUNT(r) FROM Registration r WHERE r.topic = t AND r.status = 'APPROVED') < t.maxStudents")
    Page<Topic> findAvailableTopicsBySemester(@Param("semester") String semester, Pageable pageable);
    
    @Query("SELECT t FROM Topic t WHERE " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.technology) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Topic> searchTopics(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT t FROM Topic t WHERE t.status = :status AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Topic> searchTopicsByStatus(@Param("keyword") String keyword, 
                                      @Param("status") TopicStatus status, 
                                      Pageable pageable);
    
    List<Topic> findByDepartmentAndSemester(String department, String semester);
    
    List<Topic> findByMajorAndSemester(String major, String semester);
    
    @Query("SELECT COUNT(t) FROM Topic t WHERE t.status = :status AND t.semester = :semester")
    long countByStatusAndSemester(@Param("status") TopicStatus status, @Param("semester") String semester);
    
    @Query("SELECT COUNT(t) FROM Topic t WHERE t.department = :department AND t.semester = :semester")
    long countByDepartmentAndSemester(@Param("department") String department, @Param("semester") String semester);
}

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
    
    @Query("SELECT t FROM Topic t LEFT JOIN FETCH t.lecturer LEFT JOIN FETCH t.supervisor LEFT JOIN FETCH t.student LEFT JOIN FETCH t.approvedBy WHERE t.code = :code")
    Optional<Topic> findByCode(@Param("code") String code);
    
    boolean existsByCode(String code);
    
    @Query("SELECT t FROM Topic t LEFT JOIN FETCH t.lecturer LEFT JOIN FETCH t.supervisor LEFT JOIN FETCH t.student LEFT JOIN FETCH t.approvedBy WHERE t.lecturer.id = :lecturerId")
    List<Topic> findByLecturerId(@Param("lecturerId") Long lecturerId);
    
    @Query("SELECT t FROM Topic t LEFT JOIN FETCH t.lecturer LEFT JOIN FETCH t.supervisor LEFT JOIN FETCH t.student LEFT JOIN FETCH t.approvedBy WHERE t.supervisor.id = :supervisorId")
    List<Topic> findBySupervisorId(@Param("supervisorId") Long supervisorId);
    
    @Query(value = "SELECT t FROM Topic t LEFT JOIN FETCH t.lecturer LEFT JOIN FETCH t.supervisor LEFT JOIN FETCH t.student LEFT JOIN FETCH t.approvedBy WHERE t.status = :status",
           countQuery = "SELECT COUNT(t) FROM Topic t WHERE t.status = :status")
    Page<Topic> findByStatus(@Param("status") TopicStatus status, Pageable pageable);
    
    @Query(value = "SELECT t FROM Topic t LEFT JOIN FETCH t.lecturer LEFT JOIN FETCH t.supervisor LEFT JOIN FETCH t.student LEFT JOIN FETCH t.approvedBy WHERE t.lecturer.id = :lecturerId AND t.status = :status",
           countQuery = "SELECT COUNT(t) FROM Topic t WHERE t.lecturer.id = :lecturerId AND t.status = :status")
    Page<Topic> findByLecturerIdAndStatus(@Param("lecturerId") Long lecturerId, @Param("status") TopicStatus status, Pageable pageable);
    
    @Query(value = "SELECT t FROM Topic t LEFT JOIN FETCH t.lecturer LEFT JOIN FETCH t.supervisor LEFT JOIN FETCH t.student LEFT JOIN FETCH t.approvedBy WHERE t.status = 'APPROVED' AND " +
           "(SELECT COUNT(r) FROM Registration r WHERE r.topic = t AND r.status = 'APPROVED') < t.maxStudents",
           countQuery = "SELECT COUNT(t) FROM Topic t WHERE t.status = 'APPROVED' AND (SELECT COUNT(r) FROM Registration r WHERE r.topic = t AND r.status = 'APPROVED') < t.maxStudents")
    Page<Topic> findAvailableTopics(Pageable pageable);
    
    @Query(value = "SELECT t FROM Topic t LEFT JOIN FETCH t.lecturer LEFT JOIN FETCH t.supervisor LEFT JOIN FETCH t.student LEFT JOIN FETCH t.approvedBy WHERE t.status = 'APPROVED' AND t.semester = :semester AND " +
           "(SELECT COUNT(r) FROM Registration r WHERE r.topic = t AND r.status = 'APPROVED') < t.maxStudents",
           countQuery = "SELECT COUNT(t) FROM Topic t WHERE t.status = 'APPROVED' AND t.semester = :semester AND (SELECT COUNT(r) FROM Registration r WHERE r.topic = t AND r.status = 'APPROVED') < t.maxStudents")
    Page<Topic> findAvailableTopicsBySemester(@Param("semester") String semester, Pageable pageable);
    
    @Query(value = "SELECT t FROM Topic t LEFT JOIN FETCH t.lecturer LEFT JOIN FETCH t.supervisor LEFT JOIN FETCH t.student LEFT JOIN FETCH t.approvedBy WHERE " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.technology) LIKE LOWER(CONCAT('%', :keyword, '%')))",
           countQuery = "SELECT COUNT(t) FROM Topic t WHERE (LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.technology) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Topic> searchTopics(@Param("keyword") String keyword, Pageable pageable);
    
    @Query(value = "SELECT t FROM Topic t LEFT JOIN FETCH t.lecturer LEFT JOIN FETCH t.supervisor LEFT JOIN FETCH t.student LEFT JOIN FETCH t.approvedBy WHERE t.status = :status AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.code) LIKE LOWER(CONCAT('%', :keyword, '%')))",
           countQuery = "SELECT COUNT(t) FROM Topic t WHERE t.status = :status AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Topic> searchTopicsByStatus(@Param("keyword") String keyword, 
                                      @Param("status") TopicStatus status, 
                                      Pageable pageable);
    
    @Query("SELECT t FROM Topic t LEFT JOIN FETCH t.lecturer LEFT JOIN FETCH t.supervisor LEFT JOIN FETCH t.student LEFT JOIN FETCH t.approvedBy WHERE t.id = :id")
    Optional<Topic> findByIdWithRelations(@Param("id") Long id);
    
    List<Topic> findByDepartmentAndSemester(String department, String semester);
    
    List<Topic> findByMajorAndSemester(String major, String semester);
    
    @Query("SELECT COUNT(t) FROM Topic t WHERE t.status = :status AND t.semester = :semester")
    long countByStatusAndSemester(@Param("status") TopicStatus status, @Param("semester") String semester);
    
    @Query("SELECT COUNT(t) FROM Topic t WHERE t.department = :department AND t.semester = :semester")
    long countByDepartmentAndSemester(@Param("department") String department, @Param("semester") String semester);
}

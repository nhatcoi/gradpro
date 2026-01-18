package com.university.gradpro.council.repository;

import com.university.gradpro.council.entity.Council;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouncilRepository extends JpaRepository<Council, Long> {
    
    Optional<Council> findByCode(String code);
    
    boolean existsByCode(String code);
    
    List<Council> findBySemester(String semester);
    
    List<Council> findByDepartment(String department);
    
    Page<Council> findBySemesterAndActiveTrue(String semester, Pageable pageable);
    
    @Query("SELECT c FROM Council c WHERE c.department = :department AND c.semester = :semester")
    List<Council> findByDepartmentAndSemester(@Param("department") String department, 
                                               @Param("semester") String semester);
    
    @Query("SELECT c FROM Council c JOIN c.members m WHERE m.lecturer.id = :lecturerId")
    List<Council> findByMemberLecturerId(@Param("lecturerId") Long lecturerId);
    
    @Query("SELECT COUNT(c) FROM Council c WHERE c.semester = :semester")
    long countBySemester(@Param("semester") String semester);
}

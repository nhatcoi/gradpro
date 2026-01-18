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
    
    @Query("SELECT c FROM Council c LEFT JOIN FETCH c.members m LEFT JOIN FETCH m.lecturer WHERE c.code = :code")
    Optional<Council> findByCode(@Param("code") String code);
    
    boolean existsByCode(String code);
    
    @Query("SELECT DISTINCT c FROM Council c LEFT JOIN FETCH c.members m LEFT JOIN FETCH m.lecturer WHERE c.semester = :semester")
    List<Council> findBySemester(@Param("semester") String semester);
    
    List<Council> findByDepartment(String department);
    
    Page<Council> findBySemesterAndActiveTrue(String semester, Pageable pageable);
    
    @Query("SELECT c FROM Council c WHERE c.department = :department AND c.semester = :semester")
    List<Council> findByDepartmentAndSemester(@Param("department") String department, 
                                               @Param("semester") String semester);
    
    @Query("SELECT DISTINCT c FROM Council c LEFT JOIN FETCH c.members m LEFT JOIN FETCH m.lecturer JOIN m.lecturer l WHERE l.id = :lecturerId")
    List<Council> findByMemberLecturerId(@Param("lecturerId") Long lecturerId);
    
    @Query("SELECT DISTINCT c FROM Council c LEFT JOIN FETCH c.members m LEFT JOIN FETCH m.lecturer WHERE c.id = :id")
    Optional<Council> findByIdWithRelations(@Param("id") Long id);
    
    @Query("SELECT DISTINCT c FROM Council c LEFT JOIN FETCH c.schedules s LEFT JOIN FETCH s.topic t LEFT JOIN FETCH t.student st WHERE c.id = :id")
    Optional<Council> findByIdWithSchedules(@Param("id") Long id);
    
    @Query("SELECT COUNT(c) FROM Council c WHERE c.semester = :semester")
    long countBySemester(@Param("semester") String semester);
}

package com.university.gradpro.council.repository;

import com.university.gradpro.council.entity.DefenseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DefenseScheduleRepository extends JpaRepository<DefenseSchedule, Long> {
    
    List<DefenseSchedule> findByCouncilIdOrderByOrderNumberAsc(Long councilId);
    
    Optional<DefenseSchedule> findByTopicId(Long topicId);
    
    Optional<DefenseSchedule> findByStudentId(Long studentId);
    
    List<DefenseSchedule> findByCouncilIdAndCompletedFalse(Long councilId);
    
    @Query("SELECT ds FROM DefenseSchedule ds WHERE ds.council.semester = :semester AND ds.student.id = :studentId")
    Optional<DefenseSchedule> findByStudentIdAndSemester(@Param("studentId") Long studentId, 
                                                          @Param("semester") String semester);
    
    @Query("SELECT COUNT(ds) FROM DefenseSchedule ds WHERE ds.council.id = :councilId")
    int countByCouncilId(@Param("councilId") Long councilId);
    
    void deleteByCouncilId(Long councilId);
}

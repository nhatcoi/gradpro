package com.university.gradpro.evaluation.repository;

import com.university.gradpro.evaluation.entity.FinalScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinalScoreRepository extends JpaRepository<FinalScore, Long> {
    
    Optional<FinalScore> findByTopicIdAndStudentId(Long topicId, Long studentId);
    
    List<FinalScore> findByStudentId(Long studentId);
    
    List<FinalScore> findByPassed(Boolean passed);
    
    Page<FinalScore> findByPassedTrue(Pageable pageable);
    
    @Query("SELECT fs FROM FinalScore fs WHERE fs.topic.semester = :semester")
    List<FinalScore> findBySemester(@Param("semester") String semester);
    
    @Query("SELECT fs FROM FinalScore fs WHERE fs.topic.department = :department AND fs.topic.semester = :semester")
    List<FinalScore> findByDepartmentAndSemester(@Param("department") String department, 
                                                   @Param("semester") String semester);
    
    @Query("SELECT AVG(fs.finalScore) FROM FinalScore fs WHERE fs.topic.semester = :semester")
    Double calculateAverageScoreBySemester(@Param("semester") String semester);
    
    @Query("SELECT COUNT(fs) FROM FinalScore fs WHERE fs.passed = true AND fs.topic.semester = :semester")
    long countPassedBySemester(@Param("semester") String semester);
    
    @Query("SELECT COUNT(fs) FROM FinalScore fs WHERE fs.topic.semester = :semester")
    long countBySemester(@Param("semester") String semester);
}

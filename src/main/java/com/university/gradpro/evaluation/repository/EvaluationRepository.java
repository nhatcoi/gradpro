package com.university.gradpro.evaluation.repository;

import com.university.gradpro.evaluation.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    
    List<Evaluation> findByTopicIdAndStudentId(Long topicId, Long studentId);
    
    Optional<Evaluation> findByTopicIdAndStudentIdAndEvaluatorId(Long topicId, Long studentId, Long evaluatorId);
    
    List<Evaluation> findByStudentId(Long studentId);
    
    List<Evaluation> findByEvaluatorId(Long evaluatorId);
    
    List<Evaluation> findByCouncilId(Long councilId);
    
    @Query("SELECT e FROM Evaluation e WHERE e.topic.id = :topicId AND e.student.id = :studentId AND e.evaluationType = 'SUPERVISOR'")
    Optional<Evaluation> findSupervisorEvaluation(@Param("topicId") Long topicId, @Param("studentId") Long studentId);
    
    @Query("SELECT e FROM Evaluation e WHERE e.topic.id = :topicId AND e.student.id = :studentId AND e.evaluationType = 'COUNCIL'")
    List<Evaluation> findCouncilEvaluations(@Param("topicId") Long topicId, @Param("studentId") Long studentId);
    
    @Query("SELECT AVG(e.score) FROM Evaluation e WHERE e.topic.id = :topicId AND e.student.id = :studentId AND e.evaluationType = 'COUNCIL'")
    Double calculateAverageCouncilScore(@Param("topicId") Long topicId, @Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(e) FROM Evaluation e WHERE e.council.id = :councilId AND e.evaluationType = 'COUNCIL'")
    long countCouncilEvaluations(@Param("councilId") Long councilId);
}

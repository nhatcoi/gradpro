package com.university.gradpro.council.repository;

import com.university.gradpro.common.constant.CouncilRole;
import com.university.gradpro.council.entity.CouncilMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouncilMemberRepository extends JpaRepository<CouncilMember, Long> {
    
    List<CouncilMember> findByCouncilId(Long councilId);
    
    List<CouncilMember> findByLecturerId(Long lecturerId);
    
    Optional<CouncilMember> findByCouncilIdAndLecturerId(Long councilId, Long lecturerId);
    
    Optional<CouncilMember> findByCouncilIdAndRole(Long councilId, CouncilRole role);
    
    boolean existsByCouncilIdAndLecturerId(Long councilId, Long lecturerId);
    
    void deleteByCouncilId(Long councilId);
}

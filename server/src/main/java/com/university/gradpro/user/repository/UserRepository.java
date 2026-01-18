package com.university.gradpro.user.repository;

import com.university.gradpro.common.constant.RoleType;
import com.university.gradpro.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByCode(String code);
    
    Optional<User> findByEmailOrCode(String email, String code);
    
    boolean existsByEmail(String email);
    
    boolean existsByCode(String code);
    
    List<User> findByRole(RoleType role);
    
    Page<User> findByRole(RoleType role, Pageable pageable);
    
    List<User> findByRoleAndDepartment(RoleType role, String department);
    
    List<User> findByRoleAndMajor(RoleType role, String major);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.active = true")
    List<User> findActiveByRole(@Param("role") RoleType role);
    
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND " +
           "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> searchUsersByRole(@Param("keyword") String keyword, 
                                  @Param("role") RoleType role, 
                                  Pageable pageable);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.active = true")
    long countActiveByRole(@Param("role") RoleType role);
}

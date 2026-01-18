package com.university.gradpro.registration.repository;

import com.university.gradpro.registration.entity.RegistrationPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationPeriodRepository extends JpaRepository<RegistrationPeriod, Long> {
    
    Optional<RegistrationPeriod> findBySemester(String semester);
    
    List<RegistrationPeriod> findByActiveTrue();
    
    // Tìm đợt đăng ký đang diễn ra (thời gian hiện tại nằm trong khoảng startDate và endDate)
    @Query("SELECT rp FROM RegistrationPeriod rp WHERE rp.active = true AND " +
           ":now >= rp.startDate AND :now <= rp.endDate " +
           "ORDER BY rp.startDate DESC")
    Optional<RegistrationPeriod> findCurrentOpenPeriod(@Param("now") LocalDateTime now);
    
    // Tìm đợt đăng ký sắp tới gần nhất
    @Query("SELECT rp FROM RegistrationPeriod rp WHERE rp.active = true AND rp.startDate > :now " +
           "ORDER BY rp.startDate ASC")
    List<RegistrationPeriod> findUpcomingPeriods(@Param("now") LocalDateTime now);
    
    @Query("SELECT rp FROM RegistrationPeriod rp WHERE rp.semester = :semester AND rp.active = true")
    Optional<RegistrationPeriod> findActiveBySemester(@Param("semester") String semester);
    
    List<RegistrationPeriod> findByActiveTrueOrderByStartDateDesc();
}

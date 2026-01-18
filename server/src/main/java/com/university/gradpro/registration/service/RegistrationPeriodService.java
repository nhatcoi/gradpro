package com.university.gradpro.registration.service;

import com.university.gradpro.common.exception.BadRequestException;
import com.university.gradpro.common.exception.ResourceNotFoundException;
import com.university.gradpro.registration.dto.CreateRegistrationPeriodRequest;
import com.university.gradpro.registration.dto.RegistrationPeriodDto;
import com.university.gradpro.registration.entity.RegistrationPeriod;
import com.university.gradpro.registration.repository.RegistrationPeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationPeriodService {
    
    private final RegistrationPeriodRepository periodRepository;
    
    /**
     * UC-2.1: Mở đợt đăng ký
     */
    @Transactional
    public RegistrationPeriodDto createPeriod(CreateRegistrationPeriodRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Ngày bắt đầu phải trước ngày kết thúc");
        }
        
        RegistrationPeriod period = RegistrationPeriod.builder()
                .name(request.getName())
                .semester(request.getSemester())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .active(true)
                .build();
        
        period = periodRepository.save(period);
        return toDto(period);
    }
    
    /**
     * Cập nhật đợt đăng ký
     */
    @Transactional
    public RegistrationPeriodDto updatePeriod(Long id, CreateRegistrationPeriodRequest request) {
        RegistrationPeriod period = periodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đợt đăng ký", "id", id));
        
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Ngày bắt đầu phải trước ngày kết thúc");
        }
        
        period.setName(request.getName());
        period.setSemester(request.getSemester());
        period.setStartDate(request.getStartDate());
        period.setEndDate(request.getEndDate());
        period.setDescription(request.getDescription());
        
        period = periodRepository.save(period);
        return toDto(period);
    }
    
    /**
     * Đóng đợt đăng ký
     */
    @Transactional
    public RegistrationPeriodDto closePeriod(Long id) {
        RegistrationPeriod period = periodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đợt đăng ký", "id", id));
        
        period.setActive(false);
        period = periodRepository.save(period);
        return toDto(period);
    }
    
    /**
     * Mở lại đợt đăng ký
     */
    @Transactional
    public RegistrationPeriodDto openPeriod(Long id) {
        RegistrationPeriod period = periodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đợt đăng ký", "id", id));
        
        period.setActive(true);
        period = periodRepository.save(period);
        return toDto(period);
    }
    
    public RegistrationPeriodDto getPeriodById(Long id) {
        RegistrationPeriod period = periodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Đợt đăng ký", "id", id));
        return toDto(period);
    }
    
    public List<RegistrationPeriodDto> getAllPeriods() {
        return periodRepository.findByActiveTrueOrderByStartDateDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy đợt đăng ký hiện tại (entity)
     * Ưu tiên: đợt đang diễn ra (startDate <= now <= endDate)
     * Nếu không có, trả về đợt active gần nhất
     */
    public Optional<RegistrationPeriod> getCurrentOpenPeriodEntity() {
        LocalDateTime now = LocalDateTime.now();
        
        // Ưu tiên tìm đợt đang diễn ra
        Optional<RegistrationPeriod> currentPeriod = periodRepository.findCurrentOpenPeriod(now);
        if (currentPeriod.isPresent()) {
            return currentPeriod;
        }
        
        // Nếu không có đợt đang diễn ra, tìm đợt active gần nhất (sắp tới hoặc vừa kết thúc)
        List<RegistrationPeriod> activePeriods = periodRepository.findByActiveTrueOrderByStartDateDesc();
        if (!activePeriods.isEmpty()) {
            // Trả về đợt có startDate gần nhất với thời gian hiện tại
            return activePeriods.stream()
                    .min((p1, p2) -> {
                        long diff1 = Math.abs(java.time.Duration.between(now, p1.getStartDate()).toMinutes());
                        long diff2 = Math.abs(java.time.Duration.between(now, p2.getStartDate()).toMinutes());
                        return Long.compare(diff1, diff2);
                    });
        }
        
        return Optional.empty();
    }
    
    /**
     * Lấy đợt đăng ký hiện tại (DTO)
     * Ưu tiên: đợt đang diễn ra (startDate <= now <= endDate)
     * Nếu không có, trả về đợt active gần nhất
     */
    public Optional<RegistrationPeriodDto> getCurrentOpenPeriod() {
        return getCurrentOpenPeriodEntity().map(this::toDto);
    }
    
    private RegistrationPeriodDto toDto(RegistrationPeriod period) {
        return RegistrationPeriodDto.builder()
                .id(period.getId())
                .name(period.getName())
                .semester(period.getSemester())
                .startDate(period.getStartDate())
                .endDate(period.getEndDate())
                .description(period.getDescription())
                .active(period.getActive())
                .isOpen(period.isOpen())
                .createdAt(period.getCreatedAt())
                .updatedAt(period.getUpdatedAt())
                .build();
    }
}

package com.university.gradpro.user.service;

import com.university.gradpro.common.constant.RoleType;
import com.university.gradpro.common.exception.BadRequestException;
import com.university.gradpro.common.exception.DuplicateResourceException;
import com.university.gradpro.common.exception.ResourceNotFoundException;
import com.university.gradpro.common.response.PageResponse;
import com.university.gradpro.user.dto.*;
import com.university.gradpro.user.entity.User;
import com.university.gradpro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * UC-06.1: Tạo mới người dùng (Admin)
     */
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Người dùng", "mã số", request.getCode());
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Người dùng", "email", request.getEmail());
        }
        
        User user = User.builder()
                .code(request.getCode())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(request.getRole())
                .department(request.getDepartment())
                .major(request.getMajor())
                .academicYear(request.getAcademicYear())
                .academicTitle(request.getAcademicTitle())
                .active(true)
                .build();
        
        user = userRepository.save(user);
        return toDto(user);
    }
    
    /**
     * UC-06.1: Cập nhật người dùng (Admin)
     */
    @Transactional
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));
        
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Người dùng", "email", request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getDepartment() != null) {
            user.setDepartment(request.getDepartment());
        }
        if (request.getMajor() != null) {
            user.setMajor(request.getMajor());
        }
        if (request.getAcademicYear() != null) {
            user.setAcademicYear(request.getAcademicYear());
        }
        if (request.getAcademicTitle() != null) {
            user.setAcademicTitle(request.getAcademicTitle());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        
        user = userRepository.save(user);
        return toDto(user);
    }
    
    /**
     * UC-03: Cập nhật thông tin cá nhân
     */
    @Transactional
    public UserDto updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", userId));
        
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Người dùng", "email", request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        
        user = userRepository.save(user);
        return toDto(user);
    }
    
    /**
     * UC-06.1: Xóa người dùng (Admin)
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));
        
        userRepository.delete(user);
    }
    
    /**
     * UC-06.1: Vô hiệu hóa tài khoản (Admin)
     */
    @Transactional
    public UserDto deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));
        
        user.setActive(false);
        user = userRepository.save(user);
        return toDto(user);
    }
    
    /**
     * UC-06.1: Kích hoạt tài khoản (Admin)
     */
    @Transactional
    public UserDto activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));
        
        user.setActive(true);
        user = userRepository.save(user);
        return toDto(user);
    }
    
    /**
     * UC-06.1: Reset mật khẩu (Admin)
     */
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "id", id));
        return toDto(user);
    }
    
    public UserDto getUserByCode(String code) {
        User user = userRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng", "mã số", code));
        return toDto(user);
    }
    
    public PageResponse<UserDto> getAllUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return PageResponse.from(page.map(this::toDto));
    }
    
    public PageResponse<UserDto> getUsersByRole(RoleType role, Pageable pageable) {
        Page<User> page = userRepository.findByRole(role, pageable);
        return PageResponse.from(page.map(this::toDto));
    }
    
    public PageResponse<UserDto> searchUsers(String keyword, Pageable pageable) {
        Page<User> page = userRepository.searchUsers(keyword, pageable);
        return PageResponse.from(page.map(this::toDto));
    }
    
    public List<UserDto> getLecturers() {
        return userRepository.findActiveByRole(RoleType.LECTURER)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public List<UserDto> getStudents() {
        return userRepository.findActiveByRole(RoleType.STUDENT)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .code(user.getCode())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .department(user.getDepartment())
                .major(user.getMajor())
                .academicYear(user.getAcademicYear())
                .academicTitle(user.getAcademicTitle())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

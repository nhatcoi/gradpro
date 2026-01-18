package com.university.gradpro.config;

import com.university.gradpro.common.constant.RoleType;
import com.university.gradpro.user.entity.User;
import com.university.gradpro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // Tạo tài khoản Admin mặc định
        if (!userRepository.existsByCode("ADMIN001")) {
            User admin = User.builder()
                    .code("ADMIN001")
                    .email("admin@university.edu.vn")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Quản trị viên")
                    .role(RoleType.ADMIN)
                    .department("Phòng CNTT")
                    .active(true)
                    .build();
            userRepository.save(admin);
            log.info("Đã tạo tài khoản Admin mặc định: admin@university.edu.vn / admin123");
        }
        
        // Tạo tài khoản Phòng Đào tạo
        if (!userRepository.existsByCode("PDT001")) {
            User trainingDept = User.builder()
                    .code("PDT001")
                    .email("phongdaotao@university.edu.vn")
                    .password(passwordEncoder.encode("pdt123"))
                    .fullName("Nguyễn Văn A")
                    .role(RoleType.TRAINING_DEPT)
                    .department("Phòng Đào tạo")
                    .active(true)
                    .build();
            userRepository.save(trainingDept);
            log.info("Đã tạo tài khoản Phòng Đào tạo: phongdaotao@university.edu.vn / pdt123");
        }
        
        // Tạo tài khoản Trưởng Bộ môn
        if (!userRepository.existsByCode("TBM001")) {
            User deptHead = User.builder()
                    .code("TBM001")
                    .email("truongbomon@university.edu.vn")
                    .password(passwordEncoder.encode("tbm123"))
                    .fullName("PGS.TS Trần Văn B")
                    .role(RoleType.DEPT_HEAD)
                    .department("Khoa Công nghệ thông tin")
                    .major("Bộ môn Công nghệ phần mềm")
                    .academicTitle("PGS.TS")
                    .active(true)
                    .build();
            userRepository.save(deptHead);
            log.info("Đã tạo tài khoản Trưởng BM: truongbomon@university.edu.vn / tbm123");
        }
        
        // Tạo tài khoản Giảng viên
        if (!userRepository.existsByCode("GV001")) {
            User lecturer = User.builder()
                    .code("GV001")
                    .email("giangvien@university.edu.vn")
                    .password(passwordEncoder.encode("gv123"))
                    .fullName("TS. Lê Văn C")
                    .role(RoleType.LECTURER)
                    .department("Khoa Công nghệ thông tin")
                    .major("Bộ môn Công nghệ phần mềm")
                    .academicTitle("TS")
                    .active(true)
                    .build();
            userRepository.save(lecturer);
            log.info("Đã tạo tài khoản Giảng viên: giangvien@university.edu.vn / gv123");
        }
        
        // Tạo tài khoản Sinh viên
        if (!userRepository.existsByCode("SV001")) {
            User student = User.builder()
                    .code("SV001")
                    .email("sinhvien@university.edu.vn")
                    .password(passwordEncoder.encode("sv123"))
                    .fullName("Nguyễn Thị D")
                    .role(RoleType.STUDENT)
                    .department("Khoa Công nghệ thông tin")
                    .major("Công nghệ phần mềm")
                    .academicYear("K20")
                    .active(true)
                    .build();
            userRepository.save(student);
            log.info("Đã tạo tài khoản Sinh viên: sinhvien@university.edu.vn / sv123");
        }
    }
}

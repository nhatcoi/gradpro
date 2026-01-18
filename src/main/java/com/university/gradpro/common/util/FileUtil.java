package com.university.gradpro.common.util;

import com.university.gradpro.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class FileUtil {
    
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "zip", "rar", "7z"
    );
    
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
    
    public String saveFile(MultipartFile file, String subFolder) {
        validateFile(file);
        
        try {
            Path uploadPath = Paths.get(uploadDir, subFolder);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + "." + extension;
            
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            return subFolder + "/" + newFilename;
        } catch (IOException e) {
            throw new BadRequestException("Không thể lưu file: " + e.getMessage());
        }
    }
    
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(uploadDir, filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Log error but don't throw exception
        }
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File không được để trống");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File vượt quá kích thước cho phép (50MB)");
        }
        
        String extension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("Định dạng file không được hỗ trợ");
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}

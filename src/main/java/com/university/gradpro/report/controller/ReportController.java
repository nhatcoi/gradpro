package com.university.gradpro.report.controller;

import com.university.gradpro.common.response.ApiResponse;
import com.university.gradpro.report.dto.StatisticsDto;
import com.university.gradpro.report.exporter.ExcelExporter;
import com.university.gradpro.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportService reportService;
    private final ExcelExporter excelExporter;
    
    /**
     * UC-2.4: Thống kê cấp trường
     */
    @GetMapping("/university/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINING_DEPT')")
    public ResponseEntity<ApiResponse<StatisticsDto>> getUniversityStatistics(
            @RequestParam String semester) {
        StatisticsDto statistics = reportService.getUniversityStatistics(semester);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    /**
     * UC-3.5: Thống kê cấp khoa
     */
    @GetMapping("/department/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINING_DEPT', 'DEPT_HEAD')")
    public ResponseEntity<ApiResponse<StatisticsDto>> getDepartmentStatistics(
            @RequestParam String department,
            @RequestParam String semester) {
        StatisticsDto statistics = reportService.getDepartmentStatistics(department, semester);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
    
    /**
     * UC-2.5: Xuất báo cáo cấp trường
     */
    @GetMapping("/university/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINING_DEPT')")
    public ResponseEntity<byte[]> exportUniversityReport(
            @RequestParam String semester) throws IOException {
        byte[] excelBytes = excelExporter.exportScoresToExcel(semester, null);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "bang-diem-" + semester + ".xlsx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }
    
    /**
     * UC-3.6: Xuất báo cáo cấp khoa
     */
    @GetMapping("/department/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'TRAINING_DEPT', 'DEPT_HEAD')")
    public ResponseEntity<byte[]> exportDepartmentReport(
            @RequestParam String department,
            @RequestParam String semester) throws IOException {
        byte[] excelBytes = excelExporter.exportScoresToExcel(semester, department);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", 
                "bang-diem-" + department.replace(" ", "-") + "-" + semester + ".xlsx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }
}

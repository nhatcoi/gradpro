package com.university.gradpro.report.exporter;

import com.university.gradpro.evaluation.entity.FinalScore;
import com.university.gradpro.evaluation.repository.FinalScoreRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExcelExporter {
    
    private final FinalScoreRepository finalScoreRepository;
    
    /**
     * UC-2.5, UC-3.6: Xuất báo cáo điểm ra Excel
     */
    public byte[] exportScoresToExcel(String semester, String department) throws IOException {
        List<FinalScore> scores;
        if (department != null && !department.isBlank()) {
            scores = finalScoreRepository.findByDepartmentAndSemester(department, semester);
        } else {
            scores = finalScoreRepository.findBySemester(semester);
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Bảng điểm đồ án tốt nghiệp");
            
            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            
            // Create data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"STT", "MSSV", "Họ và tên", "Mã đề tài", "Tên đề tài", "GVHD",
                    "Điểm GVHD", "Điểm HĐ", "Điểm TK", "Xếp loại", "Kết quả"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Create data rows
            int rowNum = 1;
            for (FinalScore score : scores) {
                Row row = sheet.createRow(rowNum);
                
                createCell(row, 0, rowNum, dataStyle);
                createCell(row, 1, score.getStudent().getCode(), dataStyle);
                createCell(row, 2, score.getStudent().getFullName(), dataStyle);
                createCell(row, 3, score.getTopic().getCode(), dataStyle);
                createCell(row, 4, score.getTopic().getTitle(), dataStyle);
                createCell(row, 5, score.getTopic().getSupervisor() != null 
                        ? score.getTopic().getSupervisor().getFullName() : "", dataStyle);
                createCell(row, 6, score.getSupervisorScore(), dataStyle);
                createCell(row, 7, score.getCouncilScore(), dataStyle);
                createCell(row, 8, score.getFinalScore(), dataStyle);
                createCell(row, 9, score.getLetterGrade(), dataStyle);
                createCell(row, 10, score.getPassed() ? "Đạt" : "Không đạt", dataStyle);
                
                rowNum++;
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }
        cell.setCellStyle(style);
    }
}

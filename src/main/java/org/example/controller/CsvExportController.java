package org.example.controller;

import org.example.service.CsvExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class CsvExportController {
    
    private final CsvExportService csvExportService;
    
    public CsvExportController(CsvExportService csvExportService) {
        this.csvExportService = csvExportService;
    }
    
    @GetMapping("/expenses/export")
    public ResponseEntity<String> exportExpensesToCsv(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        try {
            LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
            LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;
            
            String csv = csvExportService.exportFilteredToCsv(start, end, category);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "expenses.csv");
            
            return ResponseEntity.ok().headers(headers).body(csv);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
package com.triviumbuys.controller;

import com.triviumbuys.dto.AnalyticsResponseDto;
import com.triviumbuys.services.DataAnalysisService;
import com.triviumbuys.services.FileUploadService;
import com.triviumbuys.services.PredictionService;
import com.triviumbuys.services.ReportExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:4200")
public class AnalyticsController {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private DataAnalysisService dataAnalysisService;

    @Autowired
    private PredictionService predictionService;

    @Autowired
    private ReportExportService reportExportService;

    @PostMapping("/upload-orders")
    public ResponseEntity<Map<String, Object>> uploadOrders(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            fileUploadService.uploadOrders(file);
            response.put("success", true);
            response.put("message", "Orders uploaded successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to upload orders: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/upload-products")
    public ResponseEntity<Map<String, Object>> uploadProducts(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            fileUploadService.uploadProducts(file);
            response.put("success", true);
            response.put("message", "Products uploaded successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to upload products: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/upload-customers")
    public ResponseEntity<Map<String, Object>> uploadCustomers(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            fileUploadService.uploadCustomers(file);
            response.put("success", true);
            response.put("message", "Customers uploaded successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to upload customers: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/get-report")
    public ResponseEntity<AnalyticsResponseDto> getAnalyticsReport() {
        AnalyticsResponseDto report = dataAnalysisService.generateAnalytics();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/predict-revenue")
    public ResponseEntity<Map<String, Double>> predictRevenue(@RequestParam(defaultValue = "7") int days) {
        Map<String, Double> predictions = predictionService.predictRevenueNextDays(days);
        return ResponseEntity.ok(predictions);
    }

    @GetMapping("/export-report")
    public ResponseEntity<byte[]> exportAnalyticsReport() {
        byte[] pdfBytes = reportExportService.exportReport();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=Analytics_Report.pdf")
                .body(pdfBytes);
    }
}

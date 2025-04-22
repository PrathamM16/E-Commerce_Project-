package com.triviumbuys.controller;

import com.triviumbuys.services.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    // ✅ Export Orders Report
    @GetMapping("/orders")
    public void exportOrdersReport(
            @RequestParam String format,
            @RequestParam String duration,
            HttpServletResponse response
    ) throws IOException {
        byte[] fileData = reportService.generateOrdersReport(format, duration);
        String filename = "orders_" + duration + "_report";
        setResponse(format, response, filename, fileData);
    }

    // ✅ Export Products Report
    @GetMapping("/products")
    public void exportProductsReport(
            @RequestParam String format,
            @RequestParam String duration,
            HttpServletResponse response
    ) throws IOException {
        byte[] fileData = reportService.generateProductsReport(format, duration);
        String filename = "products_" + duration + "_report";
        setResponse(format, response, filename, fileData);
    }

    // ✅ Export Customers Report
    @GetMapping("/customers")
    public void exportCustomersReport(
            @RequestParam String format,
            @RequestParam String duration,
            HttpServletResponse response
    ) throws IOException {
        byte[] fileData = reportService.generateCustomersReport(format, duration);
        String filename = "customers_" + duration + "_report";
        setResponse(format, response, filename, fileData);
    }

    // ✅ Export Dashboard Report
    @GetMapping("/dashboard")
    public void exportDashboardReport(
            @RequestParam String format,
            @RequestParam String duration,
            HttpServletResponse response
    ) throws IOException {
        byte[] fileData = reportService.generateDashboardReport(format, duration);
        String filename = "dashboard_" + duration + "_report";
        setResponse(format, response, filename, fileData);
    }

    // ✅ Common function to prepare HTTP response
    private void setResponse(String format, HttpServletResponse response, String filename, byte[] fileData) throws IOException {
        String contentType = getContentType(format);
        String extension = getFileExtension(format);
        
        // URL encode the filename to handle special characters
        String encodedFilename = URLEncoder.encode(filename + "." + extension, StandardCharsets.UTF_8.toString())
                                      .replace("+", "%20");
        
        response.setContentType(contentType);
        response.setContentLength(fileData.length);
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"" + filename + "." + extension + "\"; filename*=UTF-8''" + encodedFilename);
        
        response.getOutputStream().write(fileData);
        response.getOutputStream().flush();
    }

    // ✅ Helper to decide Content-Type
    private String getContentType(String format) {
        switch (format.toLowerCase()) {
            case "pdf":
                return MediaType.APPLICATION_PDF_VALUE;
            case "excel":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "word":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "html":
                return MediaType.TEXT_HTML_VALUE;
            default:
                throw new RuntimeException("Unsupported format: " + format);
        }
    }
    
    // ✅ Helper to get the correct file extension
    private String getFileExtension(String format) {
        switch (format.toLowerCase()) {
            case "pdf":
                return "pdf";
            case "excel":
                return "xlsx";
            case "word":
                return "docx";
            case "html":
                return "html";
            default:
                throw new RuntimeException("Unsupported format: " + format);
        }
    }
}
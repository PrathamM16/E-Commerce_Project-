package com.triviumbuys.services;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.triviumbuys.dto.AnalyticsResponseDto;
import com.triviumbuys.utils.GraphGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class ReportExportService {
    private static final Logger logger = Logger.getLogger(ReportExportService.class.getName());

    @Autowired
    private DataAnalysisService dataAnalysisService;

    @Autowired
    private PredictionService predictionService;

    public byte[] exportReport() {
        List<File> tempFiles = new ArrayList<>();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Create PDF document
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add title
            document.add(new Paragraph("TriviumBuys - Analytics Report")
                    .setBold()
                    .setFontSize(24)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            // Gather data
            AnalyticsResponseDto report = dataAnalysisService.generateAnalytics();
            Map<String, Double> predictionData = predictionService.predictRevenueNextDays(7);

            // Add KPIs section
            document.add(new Paragraph("Key Performance Indicators")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Total Revenue: ₹" + report.getTotalRevenue()).setFontSize(14));
            document.add(new Paragraph("Total Orders: " + report.getTotalOrders()).setFontSize(14));
            document.add(new Paragraph("Total Customers: " + report.getTotalCustomers()).setFontSize(14));
            document.add(new Paragraph("\n"));

            // Generate chart files with absolute paths
            String basePath = System.getProperty("java.io.tmpdir");
            logger.info("Using temp directory: " + basePath);
            
            File revenueChart = new File(basePath, "revenue_chart.png");
            File statusPieChart = new File(basePath, "status_pie_chart.png");
            File categoryPieChart = new File(basePath, "category_pie_chart.png");
            
            tempFiles.add(revenueChart);
            tempFiles.add(statusPieChart);
            tempFiles.add(categoryPieChart);
            
            // Generate charts
            GraphGenerator.createRevenueLineChart(report.getRevenueByDate(), revenueChart.getAbsolutePath());
            GraphGenerator.createOrderStatusPieChart(report.getOrderStatusCount(), statusPieChart.getAbsolutePath());
            GraphGenerator.createCategorySalesPieChart(report.getCategorySales(), categoryPieChart.getAbsolutePath());

            // Add Revenue Trend Chart
            if (revenueChart.exists()) {
                document.add(new Paragraph("Revenue Trend").setBold().setFontSize(16));
                Image revenueImage = new Image(ImageDataFactory.create(revenueChart.getAbsolutePath()));
                revenueImage.setWidth(400);
                revenueImage.setAutoScale(true);
                document.add(revenueImage);
                document.add(new Paragraph("\n"));
            } else {
                logger.warning("Revenue chart file not found at: " + revenueChart.getAbsolutePath());
            }

            // Add Order Status Chart
            if (statusPieChart.exists()) {
                document.add(new Paragraph("Order Status Distribution").setBold().setFontSize(16));
                Image statusImage = new Image(ImageDataFactory.create(statusPieChart.getAbsolutePath()));
                statusImage.setWidth(350);
                statusImage.setAutoScale(true);
                document.add(statusImage);
                document.add(new Paragraph("\n"));
            } else {
                logger.warning("Status pie chart file not found at: " + statusPieChart.getAbsolutePath());
            }

            // Add Category Sales Chart
            if (categoryPieChart.exists()) {
                document.add(new Paragraph("Sales by Category").setBold().setFontSize(16));
                Image categoryImage = new Image(ImageDataFactory.create(categoryPieChart.getAbsolutePath()));
                categoryImage.setWidth(350);
                categoryImage.setAutoScale(true);
                document.add(categoryImage);
                document.add(new Paragraph("\n"));
            } else {
                logger.warning("Category pie chart file not found at: " + categoryPieChart.getAbsolutePath());
            }

            // Add Future Revenue Prediction
            document.add(new Paragraph("Revenue Prediction (Next 7 Days)")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));
            
            for (Map.Entry<String, Double> entry : predictionData.entrySet()) {
                document.add(new Paragraph(entry.getKey() + ": ₹" + String.format("%.2f", entry.getValue())));
            }

            // Close document
            document.close();
            pdfDoc.close();
            writer.close();
            
            logger.info("PDF report generated successfully");
            return outputStream.toByteArray();

        } catch (Exception e) {
            logger.severe("Failed to export report: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to export report: " + e.getMessage(), e);
        } finally {
            // Clean up temporary files
            for (File file : tempFiles) {
                try {
                    if (file.exists()) {
                        file.delete();
                    }
                } catch (Exception e) {
                    logger.warning("Failed to delete temporary file: " + file.getAbsolutePath());
                }
            }
        }
    }
}
package com.triviumbuys.utils;

import com.triviumbuys.entity.Order;
import com.triviumbuys.entity.Product;
import com.triviumbuys.entity.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ReportGenerator {

    // --- 1. Generate PDF ---
    public static <T> byte[] generatePdf(List<T> data) throws IOException {
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);

            document.open();
            document.add(new Paragraph("Report"));

            PdfPTable table = generatePdfTable(data);
            document.add(table);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IOException("Failed to generate PDF", e);
        }
    }

    private static <T> PdfPTable generatePdfTable(List<T> data) {
        PdfPTable table = new PdfPTable(3); // default 3 columns

        if (!data.isEmpty()) {
            T first = data.get(0);
            if (first instanceof Order) {
                table = new PdfPTable(4);
                table.addCell("Order ID");
                table.addCell("Customer ID");
                table.addCell("Total Amount");
                table.addCell("Status");

                for (T item : data) {
                    Order order = (Order) item;
                    table.addCell(String.valueOf(order.getId()));
                    table.addCell(String.valueOf(order.getCustomerId()));
                    table.addCell(String.valueOf(order.getTotalAmount()));
                    table.addCell(order.getStatus().toString());
                }
            } else if (first instanceof Product) {
                table = new PdfPTable(3);
                table.addCell("Product ID");
                table.addCell("Name");
                table.addCell("Price");

                for (T item : data) {
                    Product product = (Product) item;
                    table.addCell(String.valueOf(product.getId()));
                    table.addCell(product.getName());
                    table.addCell(String.valueOf(product.getPrice()));
                }
            } else if (first instanceof User) {
                table = new PdfPTable(3);
                table.addCell("User ID");
                table.addCell("Username");
                table.addCell("Email");

                for (T item : data) {
                    User user = (User) item;
                    table.addCell(String.valueOf(user.getId()));
                    table.addCell(user.getUsername());
                    table.addCell(user.getEmail());
                }
            }
        }

        return table;
    }

    // --- 2. Generate Excel ---
    public static <T> byte[] generateExcel(List<T> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Report");

            if (!data.isEmpty()) {
                T first = data.get(0);
                Row headerRow = sheet.createRow(0);

                if (first instanceof Order) {
                    headerRow.createCell(0).setCellValue("Order ID");
                    headerRow.createCell(1).setCellValue("Customer ID");
                    headerRow.createCell(2).setCellValue("Total Amount");
                    headerRow.createCell(3).setCellValue("Status");

                    int rowIdx = 1;
                    for (T item : data) {
                        Order order = (Order) item;
                        Row row = sheet.createRow(rowIdx++);
                        row.createCell(0).setCellValue(order.getId());
                        row.createCell(1).setCellValue(order.getCustomerId());
                        row.createCell(2).setCellValue(order.getTotalAmount());
                        row.createCell(3).setCellValue(order.getStatus().toString());
                    }
                } else if (first instanceof Product) {
                    headerRow.createCell(0).setCellValue("Product ID");
                    headerRow.createCell(1).setCellValue("Name");
                    headerRow.createCell(2).setCellValue("Price");

                    int rowIdx = 1;
                    for (T item : data) {
                        Product product = (Product) item;
                        Row row = sheet.createRow(rowIdx++);
                        row.createCell(0).setCellValue(product.getId());
                        row.createCell(1).setCellValue(product.getName());
                        row.createCell(2).setCellValue(product.getPrice());
                    }
                } else if (first instanceof User) {
                    headerRow.createCell(0).setCellValue("User ID");
                    headerRow.createCell(1).setCellValue("Username");
                    headerRow.createCell(2).setCellValue("Email");

                    int rowIdx = 1;
                    for (T item : data) {
                        User user = (User) item;
                        Row row = sheet.createRow(rowIdx++);
                        row.createCell(0).setCellValue(user.getId());
                        row.createCell(1).setCellValue(user.getUsername());
                        row.createCell(2).setCellValue(user.getEmail());
                    }
                }
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IOException("Failed to generate Excel", e);
        }
    }

    // --- 3. Generate Word (Simplified) ---
    public static <T> byte[] generateWord(List<T> data) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>Report</h1><table border='1'>");

        if (!data.isEmpty()) {
            T first = data.get(0);

            if (first instanceof Order) {
                sb.append("<tr><th>Order ID</th><th>Customer ID</th><th>Total Amount</th><th>Status</th></tr>");
                for (T item : data) {
                    Order order = (Order) item;
                    sb.append("<tr>")
                      .append("<td>").append(order.getId()).append("</td>")
                      .append("<td>").append(order.getCustomerId()).append("</td>")
                      .append("<td>").append(order.getTotalAmount()).append("</td>")
                      .append("<td>").append(order.getStatus()).append("</td>")
                      .append("</tr>");
                }
            } else if (first instanceof Product) {
                sb.append("<tr><th>Product ID</th><th>Name</th><th>Price</th></tr>");
                for (T item : data) {
                    Product product = (Product) item;
                    sb.append("<tr>")
                      .append("<td>").append(product.getId()).append("</td>")
                      .append("<td>").append(product.getName()).append("</td>")
                      .append("<td>").append(product.getPrice()).append("</td>")
                      .append("</tr>");
                }
            } else if (first instanceof User) {
                sb.append("<tr><th>User ID</th><th>Username</th><th>Email</th></tr>");
                for (T item : data) {
                    User user = (User) item;
                    sb.append("<tr>")
                      .append("<td>").append(user.getId()).append("</td>")
                      .append("<td>").append(user.getUsername()).append("</td>")
                      .append("<td>").append(user.getEmail()).append("</td>")
                      .append("</tr>");
                }
            }
        }

        sb.append("</table>");

        return sb.toString().getBytes();
    }

    // --- 4. Generate HTML ---
    public static <T> byte[] generateHtml(List<T> data) throws IOException {
        return generateWord(data);  // Simple: HTML and Word format same in this case
    }
}

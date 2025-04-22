package com.triviumbuys.services;

import java.io.IOException;

public interface ReportService {
    byte[] generateOrdersReport(String format, String duration) throws IOException;
    byte[] generateProductsReport(String format, String duration) throws IOException;
    byte[] generateCustomersReport(String format, String duration) throws IOException;
    byte[] generateDashboardReport(String format, String duration) throws IOException;
}

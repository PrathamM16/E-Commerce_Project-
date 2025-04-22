package com.triviumbuys.services;

import com.triviumbuys.entity.Order;
import com.triviumbuys.entity.Product;
import com.triviumbuys.entity.User;
import com.triviumbuys.repository.OrderRepository;
import com.triviumbuys.repository.ProductRepository;
import com.triviumbuys.repository.UserRepository;
import com.triviumbuys.utils.ReportGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public byte[] generateOrdersReport(String format, String duration) throws IOException {
        List<Order> orders = fetchOrdersByDuration(duration);
        return generateReport(orders, format);
    }

    @Override
    public byte[] generateProductsReport(String format, String duration) throws IOException {
        List<Product> products = fetchProductsByDuration(duration);
        return generateReport(products, format);
    }

    @Override
    public byte[] generateCustomersReport(String format, String duration) throws IOException {
        List<User> customers = fetchCustomersByDuration(duration);
        return generateReport(customers, format);
    }

    @Override
    public byte[] generateDashboardReport(String format, String duration) throws IOException {
        List<Order> orders = fetchOrdersByDuration(duration);
        return generateReport(orders, format);
    }

    // --- Common Report Generator ---
    private <T> byte[] generateReport(List<T> data, String format) throws IOException {
        switch (format.toLowerCase()) {
            case "pdf":
                return ReportGenerator.generatePdf(data);
            case "excel":
                return ReportGenerator.generateExcel(data);
            case "word":
                return ReportGenerator.generateWord(data);
            case "html":
                return ReportGenerator.generateHtml(data);
            default:
                throw new RuntimeException("Unsupported format: " + format);
        }
    }

    // --- Helper methods ---
    private List<Order> fetchOrdersByDuration(String duration) {
        if ("today".equalsIgnoreCase(duration)) {
            LocalDate today = LocalDate.now();
            return orderRepository.findByOrderDateBetween(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay().minusNanos(1)
            );
        } else {
            return orderRepository.findAll(); // fallback for other durations like 'all'
        }
    }


    private List<Product> fetchProductsByDuration(String duration) {
        return productRepository.findAll(); // TODO: Add proper filtering
    }

    private List<User> fetchCustomersByDuration(String duration) {
        return userRepository.findAll(); // TODO: Add proper filtering
    }
}
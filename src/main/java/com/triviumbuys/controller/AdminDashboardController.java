package com.triviumbuys.controller;

import com.triviumbuys.dto.DashboardResponse;
import com.triviumbuys.dto.RecentOrderDto;
import com.triviumbuys.entity.Category;
import com.triviumbuys.entity.Order;
import com.triviumbuys.entity.Product;
import com.triviumbuys.entity.User;
import com.triviumbuys.repository.CategoryRepository;
import com.triviumbuys.repository.OrderRepository;
import com.triviumbuys.repository.ProductRepository;
import com.triviumbuys.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/overview")
    public DashboardResponse getDashboardOverview() {

        DashboardResponse response = new DashboardResponse();

        // Total Revenue (sum of all order totals)
        Double totalRevenue = orderRepository.findAll().stream()
                .filter(order -> order.getTotalAmount() != null)
                .mapToDouble(Order::getTotalAmount)
                .sum();
        response.setTotalRevenue(Math.round(totalRevenue));

        // Total Orders
        response.setTotalOrders(orderRepository.count());

        // Total Customers
        response.setTotalCustomers(userRepository.count());

        // Total Products
        response.setTotalProducts(productRepository.count());

        // Recent 5 Orders
        List<RecentOrderDto> recentOrders = orderRepository.findTop5ByOrderByOrderDateDesc()
                .stream()
                .map(order -> {
                    RecentOrderDto dto = new RecentOrderDto();
                    dto.setOrderId(order.getId());

                    Optional<User> optionalUser = userRepository.findById(order.getCustomerId());
                    String customerName = optionalUser.map(User::getName).orElse("Unknown");
                    dto.setCustomerName(customerName);

                    dto.setStatus(order.getStatus().name());
                    dto.setDate(order.getOrderDate().toString());
                    dto.setTotal(order.getTotalAmount());
                    return dto;
                })
                .collect(Collectors.toList());
        response.setRecentOrders(recentOrders);

        // Sales by Category (pie chart)
        List<String> categoryNames = new ArrayList<>();
        List<Long> salesPerCategory = new ArrayList<>();

        for (Category category : categoryRepository.findAll()) {
            categoryNames.add(category.getName());
            long count = category.getProducts() != null
                    ? category.getProducts().stream().mapToLong(Product::getStock).sum()
                    : 0L;
            salesPerCategory.add(count);
        }
        response.setCategories(categoryNames);
        response.setSalesPerCategory(salesPerCategory);

        // Calculate Monthly Revenue
        List<Double> monthlyRevenue = calculateMonthlyRevenue();
        response.setRevenuePerMonth(monthlyRevenue);

        return response;
    }
    
    /**
     * Calculates the monthly revenue for the past 12 months
     * @return List of revenues for each month (index 0 = January, 11 = December)
     */
    private List<Double> calculateMonthlyRevenue() {
        List<Double> monthlyRevenue = new ArrayList<>(Collections.nCopies(12, 0.0));
        
        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            if (order.getOrderDate() != null && order.getTotalAmount() != null) {
                int month = order.getOrderDate().getMonthValue(); // 1-12
                monthlyRevenue.set(month - 1, monthlyRevenue.get(month - 1) + order.getTotalAmount());
            }
        }
        
        return monthlyRevenue;
    }
}
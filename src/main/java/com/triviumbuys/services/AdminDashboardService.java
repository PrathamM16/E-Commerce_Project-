package com.triviumbuys.services;

import com.triviumbuys.dto.DashboardResponse;
import com.triviumbuys.dto.RecentOrderDto;
import com.triviumbuys.repository.*;
import com.triviumbuys.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public DashboardResponse getDashboardData() {
        DashboardResponse response = new DashboardResponse();

        // Total Revenue
        double totalRevenue = orderRepository.findAll().stream()
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

        // Recent Orders
        response.setRecentOrders(
            orderRepository.findTop5ByOrderByOrderDateDesc()
                .stream()
                .map(order -> {
                    RecentOrderDto dto = new RecentOrderDto();
                    dto.setOrderId(order.getId());
                    Optional<User> userOpt = userRepository.findById(order.getCustomerId());
                    dto.setCustomerName(userOpt.map(User::getName).orElse("Unknown"));
                    dto.setStatus(order.getStatus().name());
                    dto.setDate(order.getOrderDate().toString());
                    dto.setTotal(order.getTotalAmount());
                    return dto;
                })
                .collect(Collectors.toList())
        );

        // Pie Chart: Sales by Category
        List<String> categories = new ArrayList<>();
        List<Long> salesPerCategory = new ArrayList<>();

        for (Category category : categoryRepository.findAll()) {
            categories.add(category.getName());
            long salesCount = category.getProducts() != null
                    ? category.getProducts().stream().mapToLong(Product::getStock).sum()
                    : 0L;
            salesPerCategory.add(salesCount);
        }

        response.setCategories(categories);
        response.setSalesPerCategory(salesPerCategory);

        // ✅ Set Monthly Revenue also!
        List<Double> monthlyRevenue = calculateMonthlyRevenue();
        response.setRevenuePerMonth(monthlyRevenue);

        return response;
    }

    // ✅ New method to calculate monthly revenue
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

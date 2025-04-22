package com.triviumbuys.dto;

import java.util.List;
import java.util.Map;

public class AnalyticsResponseDto {
    private Double totalRevenue;
    private Integer totalOrders;
    private Integer totalCustomers;
    private Map<String, Double> revenueByDate; // Date -> Revenue
    private Map<String, Integer> orderStatusCount; // Placed/Delivered
    private List<Map<String, Object>> topSellingProducts; // List of {productName, quantitySold}
    private Map<String, Double> categorySales; // Category -> Total Revenue
    private List<Map<String, Object>> lowStockProducts; // List of {productName, stockLeft}

    // Getters and Setters
    public Double getTotalRevenue() {
        return totalRevenue;
    }
    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    public Integer getTotalOrders() {
        return totalOrders;
    }
    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }
    public Integer getTotalCustomers() {
        return totalCustomers;
    }
    public void setTotalCustomers(Integer totalCustomers) {
        this.totalCustomers = totalCustomers;
    }
    public Map<String, Double> getRevenueByDate() {
        return revenueByDate;
    }
    public void setRevenueByDate(Map<String, Double> revenueByDate) {
        this.revenueByDate = revenueByDate;
    }
    public Map<String, Integer> getOrderStatusCount() {
        return orderStatusCount;
    }
    public void setOrderStatusCount(Map<String, Integer> orderStatusCount) {
        this.orderStatusCount = orderStatusCount;
    }
    public List<Map<String, Object>> getTopSellingProducts() {
        return topSellingProducts;
    }
    public void setTopSellingProducts(List<Map<String, Object>> topSellingProducts) {
        this.topSellingProducts = topSellingProducts;
    }
    public Map<String, Double> getCategorySales() {
        return categorySales;
    }
    public void setCategorySales(Map<String, Double> categorySales) {
        this.categorySales = categorySales;
    }
    public List<Map<String, Object>> getLowStockProducts() {
        return lowStockProducts;
    }
    public void setLowStockProducts(List<Map<String, Object>> lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }
}

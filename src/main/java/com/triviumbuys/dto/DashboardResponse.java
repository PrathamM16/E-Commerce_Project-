package com.triviumbuys.dto;

import java.util.List;

public class DashboardResponse {
    private Long totalRevenue;
    private Long totalOrders;
    private Long totalCustomers;
    private Long totalProducts;
    private List<RecentOrderDto> recentOrders;
    private List<String> categories;
    private List<Long> salesPerCategory;

    // ✅ New field for monthly revenue
    private List<Double> revenuePerMonth;

    // --- Getters and Setters ---

    public Long getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Long totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(Long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public Long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public List<RecentOrderDto> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(List<RecentOrderDto> recentOrders) {
        this.recentOrders = recentOrders;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<Long> getSalesPerCategory() {
        return salesPerCategory;
    }

    public void setSalesPerCategory(List<Long> salesPerCategory) {
        this.salesPerCategory = salesPerCategory;
    }

    public List<Double> getRevenuePerMonth() {
        return revenuePerMonth;
    }

    public void setRevenuePerMonth(List<Double> revenuePerMonth) {
        this.revenuePerMonth = revenuePerMonth;
    }

    public DashboardResponse() {
    }
}

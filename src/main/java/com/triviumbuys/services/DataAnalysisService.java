package com.triviumbuys.services;

import com.triviumbuys.dto.AnalyticsResponseDto;
import com.triviumbuys.entity.UploadedCustomer;
import com.triviumbuys.entity.UploadedOrder;
import com.triviumbuys.entity.UploadedProduct;
import com.triviumbuys.utils.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DataAnalysisService {

    @Autowired
    private FileUploadService fileUploadService;

    public AnalyticsResponseDto generateAnalytics() {
        List<UploadedOrder> orders = fileUploadService.getUploadedOrders();
        List<UploadedProduct> products = fileUploadService.getUploadedProducts();
        List<UploadedCustomer> customers = fileUploadService.getUploadedCustomers();

        double totalRevenue = 0;
        int totalOrders = 0;
        int totalCustomers = customers != null ? customers.size() : 0;

        Map<String, Double> revenueByDate = new TreeMap<>();
        Map<String, Integer> orderStatusCount = new HashMap<>();
        Map<String, Double> categorySales = new HashMap<>();
        List<Map<String, Object>> lowStockProducts = new ArrayList<>();

        if (orders != null) {
            for (UploadedOrder order : orders) {
                if (order.getTotalAmount() != null) {
                    totalRevenue += order.getTotalAmount();
                }

                if (order.getStatus() != null) {
                    orderStatusCount.put(order.getStatus(),
                        orderStatusCount.getOrDefault(order.getStatus(), 0) + 1);
                }

                if (order.getDate() != null && order.getTotalAmount() != null) {
                    revenueByDate.put(order.getDate(),
                        revenueByDate.getOrDefault(order.getDate(), 0.0) + order.getTotalAmount());
                }

                if (order.getProductDetails() != null) {
                    List<Map<String, Object>> productsInOrder = FileHelper.parseProductDetails(order.getProductDetails());
                    for (Map<String, Object> p : productsInOrder) {
                        Long productId = (Long) p.get("productId");
                        Integer quantity = (Integer) p.get("quantity");

                        if (productId != null && quantity != null && products != null) {
                            UploadedProduct product = findProductById(products, productId);
                            if (product != null && product.getCategory() != null && product.getPrice() != null) {
                                double totalProductRevenue = product.getPrice() * quantity;
                                categorySales.put(
                                    product.getCategory(),
                                    categorySales.getOrDefault(product.getCategory(), 0.0) + totalProductRevenue
                                );
                            }
                        }
                    }
                }
            }
            totalOrders = orders.size();
        }

        if (products != null) {
            for (UploadedProduct product : products) {
                if (product.getStock() != null && product.getStock() <= 10) {
                    Map<String, Object> lowStock = new HashMap<>();
                    lowStock.put("productName", product.getName());
                    lowStock.put("stockLeft", product.getStock());
                    lowStockProducts.add(lowStock);
                }
            }
        }

        AnalyticsResponseDto response = new AnalyticsResponseDto();
        response.setTotalRevenue(totalRevenue);
        response.setTotalOrders(totalOrders);
        response.setTotalCustomers(totalCustomers);
        response.setRevenueByDate(revenueByDate);
        response.setOrderStatusCount(orderStatusCount);
        response.setCategorySales(categorySales);
        response.setLowStockProducts(lowStockProducts);

        return response;
    }

    private UploadedProduct findProductById(List<UploadedProduct> products, Long id) {
        if (products == null) return null;
        for (UploadedProduct p : products) {
            if (p.getId() != null && p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }
}

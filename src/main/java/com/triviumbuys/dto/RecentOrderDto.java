package com.triviumbuys.dto;

import java.util.List;

public class RecentOrderDto {
    private Long orderId;
    private String customerName;
    private String status;
    private String date;
    private Double total;
    private List<OrderItemResponseDto> items; // 🆕

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public List<OrderItemResponseDto> getItems() { return items; }
    public void setItems(List<OrderItemResponseDto> items) { this.items = items; }
}

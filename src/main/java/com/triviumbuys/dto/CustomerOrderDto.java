package com.triviumbuys.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CustomerOrderDto {
    private Long id;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private String status;
    private String address;   // ✅ 🆕 Added Address field
    private List<OrderItemResponseDto> items; 

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }  // ✅ Setter for address

    public List<OrderItemResponseDto> getItems() { return items; }
    public void setItems(List<OrderItemResponseDto> items) { this.items = items; }
}

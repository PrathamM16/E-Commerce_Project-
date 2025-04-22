package com.triviumbuys.dto;

public class OrderResponseDto {
    private Long orderId;
    private String message;
    private Double totalAmount;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
}

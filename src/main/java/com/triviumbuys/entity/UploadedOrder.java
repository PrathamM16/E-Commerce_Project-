package com.triviumbuys.entity;

public class UploadedOrder {
    private Long orderId;
    private String customerName;
    private String status;
    private String date;
    private Double totalAmount;
    private String productDetails;

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public Double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    public String getProductDetails() {
        return productDetails;
    }
    public void setProductDetails(String productDetails) {
        this.productDetails = productDetails;
    }
}

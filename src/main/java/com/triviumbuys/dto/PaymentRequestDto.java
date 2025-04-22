package com.triviumbuys.dto;

import java.time.LocalDate;

public class PaymentRequestDto {
    
    private Long orderId;
    private String paymentMethod;   // Example: "Card", "UPI", "Cash on Delivery"
    private String cardNumber;       // Masked or validated at frontend (only store last 4 digits if required)
    private String cardHolderName;
    private Double amount;
    private LocalDate transactionDate;

    // ------------------ GETTERS AND SETTERS ------------------

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
}

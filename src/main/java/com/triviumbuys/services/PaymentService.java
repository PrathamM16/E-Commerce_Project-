package com.triviumbuys.services;

import com.triviumbuys.dto.PaymentRequestDto;
import com.triviumbuys.entity.Payment;
import com.triviumbuys.repository.OrderRepository;
import com.triviumbuys.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    public Payment processPayment(PaymentRequestDto paymentRequest) {
        // Validate: Check if orderId is present
        if (paymentRequest.getOrderId() == null) {
            throw new RuntimeException("Order ID must not be null while processing payment.");
        }

        // Validate: Check if Order exists in DB
        if (!orderRepository.existsById(paymentRequest.getOrderId())) {
            throw new RuntimeException("Order not found for payment.");
        }

        // Create a new Payment object
        Payment payment = new Payment();
        payment.setOrderId(paymentRequest.getOrderId());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        
        // Mask the card number before saving (store only last 4 digits)
        payment.setCardNumber(maskCardNumber(paymentRequest.getCardNumber()));

        payment.setCardHolderName(paymentRequest.getCardHolderName());
        payment.setAmount(paymentRequest.getAmount());
        payment.setTransactionDate(LocalDate.now());

        // Save Payment into Database
        return paymentRepository.save(payment);
    }

    // Helper function to mask card number like "XXXX-XXXX-XXXX-1234"
    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() >= 4) {
            String last4Digits = cardNumber.substring(cardNumber.length() - 4);
            return "XXXX-XXXX-XXXX-" + last4Digits;
        }
        return "Invalid Card";
    }
}

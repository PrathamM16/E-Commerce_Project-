package com.triviumbuys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.triviumbuys.dto.PaymentRequestDto;
import com.triviumbuys.services.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequestDto paymentRequestDto) {
        paymentService.processPayment(paymentRequestDto);
        return ResponseEntity.ok("Payment successful and saved!");
    }

}

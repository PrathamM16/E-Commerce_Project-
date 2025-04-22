package com.triviumbuys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.triviumbuys.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

}

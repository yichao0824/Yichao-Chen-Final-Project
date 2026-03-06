package com.chuwa.payment.repository;

import com.chuwa.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,String> {
    Optional<Payment> findByIdempotencyKey(String key);
}

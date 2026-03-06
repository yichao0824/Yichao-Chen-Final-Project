package com.chuwa.payment.service;

import com.chuwa.payment.dto.PaymentRequest;
import com.chuwa.payment.entity.Payment;
import com.chuwa.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public Payment pay(PaymentRequest req) {

        Optional<Payment> existing =
                paymentRepository.findByIdempotencyKey(req.getIdempotencyKey());
        if (existing.isPresent()) {
            return existing.get();
        }

        Payment payment = Payment.builder()
                .orderId(req.getOrderId())
                .amount(req.getAmount())
                .status("SUCCESS")
                .idempotencyKey(req.getIdempotencyKey())
                .createAt(Instant.now())
                .build();

        paymentRepository.save(payment);
        kafkaTemplate.send("payment-events", req.getOrderId());
        return  payment;
    }
}

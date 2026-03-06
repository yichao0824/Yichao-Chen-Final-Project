package com.chuwa.payment.service;

import com.chuwa.payment.dto.PaymentEvent;
import com.chuwa.payment.dto.PaymentRequest;
import com.chuwa.payment.dto.UpdatePaymentRequest;
import com.chuwa.payment.entity.Payment;
import com.chuwa.payment.entity.PaymentStatus;
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
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public Payment pay(PaymentRequest req) {
        Optional<Payment> existing = paymentRepository.findByIdempotencyKey(req.getIdempotencyKey());
        if (existing.isPresent()) {
            return existing.get();
        }

        Payment payment = Payment.builder()
                .orderId(req.getOrderId())
                .amount(req.getAmount())
                .status(PaymentStatus.SUCCESS)
                .idempotencyKey(req.getIdempotencyKey())
                .createAt(Instant.now())
                .build();

        payment = paymentRepository.save(payment);

        publishEvent(PaymentEvent.builder()
                .eventType("PAYMENT_SUCCESS")
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .createdAt(payment.getCreateAt())
                .build());

        return payment;
    }

    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public Payment updatePayment(String paymentId, UpdatePaymentRequest req) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new RuntimeException("Refunded payment cannot be updated");
        }

        if (req.getAmount() != null) {
            payment.setAmount(req.getAmount());
        }

        payment = paymentRepository.save(payment);

        publishEvent(PaymentEvent.builder()
                .eventType("PAYMENT_UPDATED")
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .createdAt(Instant.now())
                .build());

        return payment;
    }

    public Payment refund(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            return payment;
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment = paymentRepository.save(payment);

        publishEvent(PaymentEvent.builder()
                .eventType("PAYMENT_REFUNDED")
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .createdAt(Instant.now())
                .build());

        return payment;
    }

    private void publishEvent(PaymentEvent event) {
        kafkaTemplate.send("payment-events-v2", event.getOrderId(), event);
    }
}
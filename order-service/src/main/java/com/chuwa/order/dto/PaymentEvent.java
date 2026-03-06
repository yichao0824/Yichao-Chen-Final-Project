package com.chuwa.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class PaymentEvent {
    private String eventType;   // PAYMENT_SUCCESS / PAYMENT_REFUNDED / PAYMENT_UPDATED
    private String paymentId;
    private String orderId;
    private BigDecimal amount;
    private String status;
    private Instant createdAt;
}
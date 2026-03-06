package com.chuwa.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class PaymentEvent {
    private String eventType;
    private String paymentId;
    private String orderId;
    private BigDecimal amount;
    private String status;
    private Instant createdAt;
}
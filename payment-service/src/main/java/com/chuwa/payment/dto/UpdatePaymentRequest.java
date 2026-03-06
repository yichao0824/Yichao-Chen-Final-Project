package com.chuwa.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdatePaymentRequest {
    private BigDecimal amount;
}
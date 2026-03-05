package com.chuwa.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private String orderId;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private List<String> items;
    private Instant createdAt;
    private Instant updatedAt;
}

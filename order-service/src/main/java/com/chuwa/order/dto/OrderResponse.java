package com.chuwa.order.dto;

import com.chuwa.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponse {

    private Long id;

    private Long userId;

    private String itemId;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    private OrderStatus status;

    private LocalDateTime createdAt;

}
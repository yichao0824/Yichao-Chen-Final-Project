package com.chuwa.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("orders_by_id")
public class OrderEntity {

    @PrimaryKey
    private Long id;

    private Long userId;
    private String itemId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    private OrderStatus status;

    private LocalDateTime createdAt;
}
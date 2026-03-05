package com.chuwa.order.entity;

import lombok.*;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Table("orders_by_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(onConstructor_ = @PersistenceCreator)
@Builder
public class OrderEntity {
    @PrimaryKey
    private String orderId;

    private Long userId;
    private String status; // CREATE / PAID / CANCEL

    private BigDecimal totalAmount;

    private List<String> items;

    private Instant createdAt;
    private Instant updatedAt;
}

package com.chuwa.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull
    private Long userId;

    @NotEmpty
    private List<OrderItem> items;

    @Data
    public static class OrderItem {
        @NotNull
        private String itemId;

        @NotNull
        private int qty;
    }
}

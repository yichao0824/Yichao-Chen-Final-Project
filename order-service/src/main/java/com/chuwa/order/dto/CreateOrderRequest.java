package com.chuwa.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotNull
    private Long userId;

    @NotNull
    private String itemId;

    @Min(1)
    private Integer quantity;

}
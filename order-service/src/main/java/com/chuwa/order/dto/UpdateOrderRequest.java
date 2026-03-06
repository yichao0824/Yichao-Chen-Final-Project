package com.chuwa.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderRequest {

    @NotNull
    @Min(1)
    private Integer quantity;
}
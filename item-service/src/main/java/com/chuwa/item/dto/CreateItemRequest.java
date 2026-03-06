package com.chuwa.item.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class CreateItemRequest {

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotNull
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    private String upc;

    private List<String> pictureUrls;

    @NotNull
    @Min(value = 0, message = "Inventory cannot be negative")
    private Integer inventory;

    private Map<String, Object> attributes;
}
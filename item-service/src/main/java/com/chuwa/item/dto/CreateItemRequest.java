package com.chuwa.item.dto;

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
    private BigDecimal price;

    private String upc;
    private List<String> pictureUrls;

    @NotNull
    private Integer inventory;

    private Map<String, Object> attributes;
}

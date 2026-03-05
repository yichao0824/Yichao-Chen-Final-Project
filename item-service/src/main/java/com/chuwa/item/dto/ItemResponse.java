package com.chuwa.item.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ItemResponse {
    private String id;
    private String name;
    private BigDecimal price;
    private String upc;
    private List<String> pictureUrls;
    private Integer inventory;
    private Map<String, Object> attributes;
}

package com.chuwa.item.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Document(collection = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    @Id
    private String id;
    private String name;

    private BigDecimal price;
    private String upc;
    private List<String> pictureUrls;

    private Integer inventory;

    private Map<String, Object> attributes;
}

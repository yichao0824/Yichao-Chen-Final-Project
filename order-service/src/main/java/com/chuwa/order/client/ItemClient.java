package com.chuwa.order.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ItemClient {

    private final RestTemplate restTemplate;

    @Value("${item.service.url:http://localhost:8082}")
    private String itemServiceBaseUrl;

    public Integer getInventory(String itemId) {
        String url = itemServiceBaseUrl + "/items/" + itemId + "/inventory";
        return restTemplate.getForObject(url, Integer.class);
    }

    public Integer decreaseInventory(String itemId, int qty) {
        String url = itemServiceBaseUrl + "/items/" + itemId + "/inventory/decrease";
        QtyRequest request = new QtyRequest();
        request.setQty(qty);
        return restTemplate.postForObject(url, request, Integer.class);
    }

    public Integer increaseInventory(String itemId, int qty) {
        String url = itemServiceBaseUrl + "/items/" + itemId + "/inventory/increase";
        QtyRequest request = new QtyRequest();
        request.setQty(qty);
        return restTemplate.postForObject(url, request, Integer.class);
    }

    @Data
    public static class QtyRequest {
        private Integer qty;
    }
}
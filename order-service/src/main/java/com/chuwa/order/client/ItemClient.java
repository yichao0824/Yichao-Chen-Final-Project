package com.chuwa.order.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ItemClient {

    private final RestTemplate restTemplate;

    // 你现在 item-service 跑在 8082
    private static final String ITEM_SERVICE_BASE_URL = "http://localhost:8082";

    public Integer getInventory(String itemId) {
        String url = ITEM_SERVICE_BASE_URL + "/items/" + itemId + "/inventory";
        return restTemplate.getForObject(url, Integer.class);
    }

    public Integer decreaseInventory(String itemId, int qty) {
        String url = ITEM_SERVICE_BASE_URL + "/items/" + itemId + "/inventory/decrease";

        QtyRequest request = new QtyRequest();
        request.setQty(qty);

        return restTemplate.postForObject(url, request, Integer.class);
    }

    @Data
    public static class QtyRequest {
        private Integer qty;
    }
}
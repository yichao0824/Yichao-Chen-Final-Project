package com.chuwa.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "itemClient", url = "${item.service.url}")
public interface ItemClient {

    @PostMapping("/items/{id}/inventory/decrease")
    Integer decreaseInventory(@PathVariable("id") String id, @RequestBody QtyRequest req);
    record  QtyRequest(int qty) {
    }
}

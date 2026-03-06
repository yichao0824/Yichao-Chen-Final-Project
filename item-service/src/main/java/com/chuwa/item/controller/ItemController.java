package com.chuwa.item.controller;

import com.chuwa.item.dto.CreateItemRequest;
import com.chuwa.item.dto.ItemResponse;
import com.chuwa.item.dto.UpdateItemRequest;
import com.chuwa.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemResponse create(@Valid @RequestBody CreateItemRequest req) {
        return itemService.create(req);
    }

    @GetMapping
    public List<ItemResponse> getAll() {
        return itemService.getAll();
    }

    @GetMapping("/{id}")
    public ItemResponse getById(@PathVariable String id) {
        return itemService.getById(id);
    }

    @PutMapping("/{id}")
    public ItemResponse update(@PathVariable String id,
                               @Valid @RequestBody UpdateItemRequest req) {
        return itemService.update(id, req);
    }

    @GetMapping("/{id}/inventory")
    public Integer getInventory(@PathVariable String id) {
        return itemService.getInventory(id);
    }

    @PostMapping("/{id}/inventory/decrease")
    public Integer decrease(@PathVariable String id, @RequestBody QtyRequest req) {
        return itemService.decreaseInventory(id, req.getQty());
    }

    @PostMapping("/{id}/inventory/increase")
    public Integer increase(@PathVariable String id, @RequestBody QtyRequest req) {
        return itemService.increaseInventory(id, req.getQty());
    }

    @Data
    public static class QtyRequest {
        private Integer qty;
    }
}
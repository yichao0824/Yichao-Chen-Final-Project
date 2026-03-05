package com.chuwa.order.controller;

import com.chuwa.order.dto.CreateOrderRequest;
import com.chuwa.order.dto.OrderResponse;
import com.chuwa.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest req) {
        return orderService.createOrder(req);
    }

    @GetMapping
    public OrderResponse get(@RequestParam("id") String id) {
        return orderService.getById(id);
    }
}

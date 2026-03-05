package com.chuwa.order.service;

import com.chuwa.order.client.ItemClient;
import com.chuwa.order.dto.CreateOrderRequest;
import com.chuwa.order.dto.OrderResponse;
import com.chuwa.order.entity.OrderEntity;
import com.chuwa.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemClient itemClient;
    public OrderResponse createOrder(CreateOrderRequest req) {
        for (CreateOrderRequest.OrderItem oi : req.getItems()) {
               itemClient.decreaseInventory(oi.getItemId(), new ItemClient.QtyRequest(oi.getQty()));
        }

        BigDecimal total = BigDecimal.ZERO;
        String orderId = UUID.randomUUID().toString();
        Instant now = Instant.now();

        List<String> items = req.getItems().stream()
                .map(i -> i.getItemId() + ":" + i.getQty())
                .collect(Collectors.toList());
        OrderEntity entity = OrderEntity.builder()
                .orderId(orderId)
                .userId(req.getUserId())
                .status("CREATED")
                .totalAmount(total)
                .items(items)
                .createdAt(now)
                .updatedAt(now)
                .build();
        orderRepository.save(entity);
        return toResponse(entity);
    }
    public OrderResponse getById(String orderId) {
        OrderEntity e = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order Not Found"));
        return toResponse(e);
    }
    public OrderResponse toResponse(OrderEntity entity) {
        return OrderResponse.builder()
                .orderId(entity.getOrderId())
                .userId(entity.getUserId())
                .status(entity.getStatus())
                .totalAmount(entity.getTotalAmount())
                .items(entity.getItems())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

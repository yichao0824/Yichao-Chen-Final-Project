package com.chuwa.order.service;

import com.chuwa.order.client.ItemClient;
import com.chuwa.order.dto.CreateOrderRequest;
import com.chuwa.order.dto.OrderResponse;
import com.chuwa.order.entity.OrderEntity;
import com.chuwa.order.entity.OrderStatus;
import com.chuwa.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemClient itemClient;

    public OrderResponse createOrder(CreateOrderRequest req) {

        Integer inventory;
        try {
            inventory = itemClient.getInventory(req.getItemId());
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Item not found");
        }

        if (inventory == null) {
            throw new RuntimeException("Item not found");
        }

        if (inventory < req.getQuantity()) {
            throw new RuntimeException("Not enough inventory");
        }

        try {
            itemClient.decreaseInventory(req.getItemId(), req.getQuantity());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 409) {
                throw new RuntimeException("Not enough inventory");
            }
            if (e.getStatusCode().value() == 404) {
                throw new RuntimeException("Item not found");
            }
            throw new RuntimeException("Failed to decrease inventory");
        }

        BigDecimal price = new BigDecimal("100"); // 先保留 demo price，后面再接 item price

        OrderEntity order = OrderEntity.builder()
                .id(System.currentTimeMillis())
                .userId(req.getUserId())
                .itemId(req.getItemId())
                .quantity(req.getQuantity())
                .unitPrice(price)
                .totalPrice(price.multiply(new BigDecimal(req.getQuantity())))
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        orderRepository.save(order);

        return toResponse(order);
    }

    public OrderResponse getOrder(Long id) {

        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return toResponse(order);
    }

    public OrderResponse cancelOrder(Long id) {

        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new RuntimeException("Cannot cancel paid order");
        }

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        return toResponse(order);
    }

    private OrderResponse toResponse(OrderEntity order) {

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .itemId(order.getItemId())
                .quantity(order.getQuantity())
                .unitPrice(order.getUnitPrice())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
package com.chuwa.order.service;

import com.chuwa.order.client.ItemClient;
import com.chuwa.order.dto.CreateOrderRequest;
import com.chuwa.order.dto.OrderResponse;
import com.chuwa.order.dto.PaymentEvent;
import com.chuwa.order.dto.UpdateOrderRequest;
import com.chuwa.order.entity.OrderEntity;
import com.chuwa.order.entity.OrderStatus;
import com.chuwa.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

        BigDecimal price = new BigDecimal("100");

        OrderEntity order = OrderEntity.builder()
                .id(System.currentTimeMillis())
                .userId(req.getUserId())
                .itemId(req.getItemId())
                .quantity(req.getQuantity())
                .unitPrice(price)
                .totalPrice(price.multiply(BigDecimal.valueOf(req.getQuantity())))
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        orderRepository.save(order);
        return toResponse(order);
    }

    public OrderResponse getOrder(Long id) {
        OrderEntity order = findOrder(id);
        return toResponse(order);
    }

    public OrderResponse updateOrder(Long id, UpdateOrderRequest req) {
        OrderEntity order = findOrder(id);

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new RuntimeException("Only CREATED orders can be updated");
        }

        int oldQty = order.getQuantity();
        int newQty = req.getQuantity();

        if (newQty == oldQty) {
            return toResponse(order);
        }

        int diff = newQty - oldQty;

        try {
            if (diff > 0) {
                itemClient.decreaseInventory(order.getItemId(), diff);
            } else {
                itemClient.increaseInventory(order.getItemId(), -diff);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 409) {
                throw new RuntimeException("Not enough inventory");
            }
            if (e.getStatusCode().value() == 404) {
                throw new RuntimeException("Item not found");
            }
            throw new RuntimeException("Failed to update inventory");
        }

        order.setQuantity(newQty);
        order.setTotalPrice(order.getUnitPrice().multiply(BigDecimal.valueOf(newQty)));

        orderRepository.save(order);
        return toResponse(order);
    }

    public OrderResponse cancelOrder(Long id) {
        OrderEntity order = findOrder(id);

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return toResponse(order);
        }

        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel paid/completed order directly");
        }

        try {
            itemClient.increaseInventory(order.getItemId(), order.getQuantity());
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to restore inventory");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return toResponse(order);
    }

    @KafkaListener(topics = "payment-events-v2", groupId = "order-service-group")
    public void consumePaymentEvent(PaymentEvent event) {
        if (event == null || event.getOrderId() == null || event.getEventType() == null) {
            return;
        }

        Long orderId;
        try {
            orderId = Long.valueOf(event.getOrderId());
        } catch (NumberFormatException e) {
            return;
        }

        OrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return;
        }

        switch (event.getEventType()) {
            case "PAYMENT_SUCCESS" -> {
                if (order.getStatus() == OrderStatus.CREATED) {
                    order.setStatus(OrderStatus.PAID);
                    orderRepository.save(order);
                }
            }
            case "PAYMENT_REFUNDED" -> {
                if (order.getStatus() != OrderStatus.CANCELLED) {
                    try {
                        itemClient.increaseInventory(order.getItemId(), order.getQuantity());
                    } catch (Exception ignored) {
                    }
                    order.setStatus(OrderStatus.CANCELLED);
                    orderRepository.save(order);
                }
            }
            default -> {
            }
        }
    }

    private OrderEntity findOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
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
package com.chuwa.order.repository;

import com.chuwa.order.entity.OrderEntity;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<OrderEntity, String> {
}

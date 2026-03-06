package com.chuwa.order.repository;

import com.chuwa.order.entity.OrderEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CassandraRepository<OrderEntity, Long> {
}
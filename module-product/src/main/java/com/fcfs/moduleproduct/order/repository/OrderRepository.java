package com.fcfs.moduleproduct.order.repository;

import com.fcfs.moduleproduct.order.entity.Order;
import com.fcfs.moduleproduct.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserId(Long userId);

    List<Order> findAllByUserIdAndOrderStatus(Long userId, OrderStatus orderStatus);

    Optional<Order> findByUserIdAndId(Long userId, Long id);
}

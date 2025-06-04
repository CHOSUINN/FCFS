package com.fcfs.fcfs.order.repository;

import com.fcfs.fcfs.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

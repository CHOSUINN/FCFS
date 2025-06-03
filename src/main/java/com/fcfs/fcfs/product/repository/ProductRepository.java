package com.fcfs.fcfs.product.repository;

import com.fcfs.fcfs.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

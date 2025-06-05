package com.fcfs.moduleorder.product.repository;

import com.fcfs.moduleorder.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

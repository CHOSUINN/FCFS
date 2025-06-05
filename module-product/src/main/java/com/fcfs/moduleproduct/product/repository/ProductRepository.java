package com.fcfs.moduleproduct.product.repository;

import com.fcfs.moduleproduct.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

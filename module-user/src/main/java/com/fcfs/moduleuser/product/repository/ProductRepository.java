package com.fcfs.moduleuser.product.repository;

import com.fcfs.moduleuser.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

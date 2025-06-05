package com.fcfs.moduleproduct.wishlist.repository;

import com.fcfs.moduleproduct.wishlist.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Optional<Wishlist> findByUser_id(Long userId);
}

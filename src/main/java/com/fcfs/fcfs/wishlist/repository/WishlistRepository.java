package com.fcfs.fcfs.wishlist.repository;

import com.fcfs.fcfs.wishlist.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Optional<Wishlist> findByUser_id(Long userId);
}

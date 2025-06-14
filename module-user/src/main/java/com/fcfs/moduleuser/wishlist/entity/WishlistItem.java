package com.fcfs.moduleuser.wishlist.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wishlist_details")
public class WishlistItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    @Setter
    @Column(nullable = false)
    private Integer quantity;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @Column(nullable = false)
    private Long productId;

    public static WishlistItem from(Long productId, Integer quantity) {
        return WishlistItem.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
    }
}

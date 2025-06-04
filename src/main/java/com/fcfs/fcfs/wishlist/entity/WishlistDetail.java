package com.fcfs.fcfs.wishlist.entity;

import com.fcfs.fcfs.product.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.Objects;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wishlist_details")
public class WishlistDetail {

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

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public static WishlistDetail from(Product product, Integer quantity) {
        return WishlistDetail.builder()
                .quantity(quantity)
                .product(product)
                .build();
    }
}

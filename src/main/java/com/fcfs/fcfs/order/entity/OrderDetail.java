package com.fcfs.fcfs.order.entity;

import com.fcfs.fcfs.product.entity.Product;
import com.fcfs.fcfs.wishlist.entity.WishlistDetail;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_details")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public static OrderDetail from(WishlistDetail detail) {
        return OrderDetail.builder()
                .quantity(detail.getQuantity())
                .product(detail.getProduct())
                .build();
    }
}

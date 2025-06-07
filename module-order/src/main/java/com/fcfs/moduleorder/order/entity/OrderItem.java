package com.fcfs.moduleorder.order.entity;

import com.fcfs.moduleorder.order.dto.WishlistItemDto;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_details")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Long productId;

    /** WishlistItemDto → OrderItem 변환용 */
    public static OrderItem from(WishlistItemDto wd) {
        return OrderItem.builder()
                .productId(wd.productId())
                .quantity(wd.quantity())
                .build();
    }
}

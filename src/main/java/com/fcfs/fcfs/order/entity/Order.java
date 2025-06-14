package com.fcfs.fcfs.order.entity;

import com.fcfs.fcfs.order.dto.request.OrderRequestDto;
import com.fcfs.fcfs.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(length = 200, nullable = false)
    private String address;

    // 주문 시간 저장
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime orderedAt;

    // 주문 상태 변경 시간 저장
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "order",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public static Order from(User user, OrderRequestDto requestDto) {
        return Order.builder()
                .orderStatus(OrderStatus.PREPARING_PRODUCT)
                .address(requestDto.address())
                .user(user)
                .build();
    }

    public void addOrderDetail(OrderDetail detail) {
        this.orderDetails.add(detail);
        detail.setOrder(this);
    }

    public void removeOrderDetail(OrderDetail detail) {
        this.orderDetails.remove(detail);
        detail.setOrder(null);
    }
}

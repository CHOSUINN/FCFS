package com.microservices.modulepayment.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Setter
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    // 결제 시간 저장
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime orderedAt;

    // 결제 상태 변경 시간 저장
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

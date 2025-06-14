package com.fcfs.moduleuser.wishlist.entity;

import com.fcfs.moduleuser.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "wishlists")
@EntityListeners(AuditingEntityListener.class)
public class Wishlist {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "wishlist",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<WishlistItem> wishlistItems = new ArrayList<>();

    public void addWishlistDetail(WishlistItem detail) {
        wishlistItems.add(detail);
        detail.setWishlist(this);
    }

    public void removeWishlistDetail(WishlistItem detail) {
        wishlistItems.remove(detail);
        detail.setWishlist(null);
    }

    // 주문 혹은 상품 전체 삭제 기능 사용시 사용하는 메서드
    public void initWishlist() {
        wishlistItems.clear();
    }

}

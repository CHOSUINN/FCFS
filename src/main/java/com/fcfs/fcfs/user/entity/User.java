package com.fcfs.fcfs.user.entity;

import com.fcfs.fcfs.global.common.Timestamped;
import com.fcfs.fcfs.order.entity.Order;
import com.fcfs.fcfs.product.entity.Product;
import com.fcfs.fcfs.user.dto.request.UserSignUpRequestDto;
import com.fcfs.fcfs.wishlist.entity.Wishlist;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(name = "users")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false, length = 64)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(unique = true, length = 32)
    private String nickname;

    @Column(length = 100)
    private String address;

    @Column(unique = true, length = 32)
    private String phoneNumber;

    @Setter
    @NotNull
    @Column(nullable = false)
    private Boolean isVerified;

    @NotNull
    @Column(nullable = false)
    private Boolean isDeleted;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRoleEnum role;

    @OneToOne(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Wishlist wishlist;

    @Builder.Default
    @OneToMany(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Product> products = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Order> orders = new ArrayList<>();

    public static User from(UserSignUpRequestDto requestDto, String encodedPassword) {
        return User.builder()
                .email(requestDto.email())
                .password(encodedPassword)
                .nickname(requestDto.nickname())
                .address(requestDto.address())
                .phoneNumber(requestDto.phoneNumber())
                .isVerified(false)
                .isDeleted(false)
                .role(UserRoleEnum.USER)
                .build();
    }

    public void assignWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
        wishlist.setUser(this);
    }

    public void removeWishlist() {
        if (this.wishlist != null) {
            this.wishlist.setUser(null);
            this.wishlist = null;
        }
    }

    public void addOrder(Order order) {
        this.orders.add(order);
        order.setUser(this);
    }

    public void removeOrder(Order order) {
        this.orders.remove(order);
        order.setUser(null);
    }


}

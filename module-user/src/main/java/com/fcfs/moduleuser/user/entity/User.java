package com.fcfs.moduleuser.user.entity;

import com.fcfs.moduleuser.global.common.Timestamped;
import com.fcfs.moduleuser.user.dto.request.UserSignUpRequestDto;
import com.fcfs.moduleuser.wishlist.entity.Wishlist;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
}

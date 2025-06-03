package com.fcfs.fcfs.user.entity;

import com.fcfs.fcfs.global.common.Timestamped;
import com.fcfs.fcfs.user.dto.request.UserSignUpRequestDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false, length = 64)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(length = 32)
    private String nickname;

    @Column(length = 100)
    private String address;

    @Column(length = 32)
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
}

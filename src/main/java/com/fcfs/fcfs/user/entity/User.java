package com.fcfs.fcfs.user.entity;

import com.fcfs.fcfs.global.common.Timestamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String address;

    private String phoneNumber;

    private Boolean isVerified;

    private Boolean isDeleted;

    private UserRoleEnum role;
}

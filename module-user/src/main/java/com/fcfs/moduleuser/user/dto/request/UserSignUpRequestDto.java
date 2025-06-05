package com.fcfs.moduleuser.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserSignUpRequestDto(

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        String password,

        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
        String nickname,

        @Size(min = 1, max = 100, message = "주소는 1자 이상 100자 이하여야 합니다.")
        String address,

        @Pattern(
                regexp = "^01[016789]-\\d{3,4}-\\d{4}$",
                message = "전화번호 형식이 올바르지 않습니다. 예) 010-1234-5678"
        )
        String phoneNumber
) {}

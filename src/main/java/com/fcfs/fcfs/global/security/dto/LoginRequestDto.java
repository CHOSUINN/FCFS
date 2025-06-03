package com.fcfs.fcfs.global.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(
        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "유효한 이메일 형식이어야 합니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하이어야 합니다.")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*()])(?=.*[a-zA-Z]).{8,20}$",
                message = "비밀번호는 숫자, 특수문자, 영문자를 모두 포함해야 합니다."
        )
        String password
) {}
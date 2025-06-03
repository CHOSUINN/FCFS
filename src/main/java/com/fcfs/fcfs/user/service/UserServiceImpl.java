package com.fcfs.fcfs.user.service;

import com.fcfs.fcfs.Email.service.EmailVerificationService;
import com.fcfs.fcfs.user.dto.UserSignUpRequestDto;
import com.fcfs.fcfs.user.dto.request.UserLoginRequestDto;
import com.fcfs.fcfs.user.entity.User;
import com.fcfs.fcfs.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    public void registerUser(UserSignUpRequestDto requestDto) throws MessagingException, UnsupportedEncodingException {
        // DB에 암호화 및 저장 진행
        String encodedPassword = passwordEncoder.encode(requestDto.password());
        User user = User.from(requestDto, encodedPassword);
        userRepository.save(user);

        // 이메일 전송
        emailVerificationService.sendVerificationEmail(user.getEmail());
    }

    public void verifyEmail(String token) {
        emailVerificationService.verifyLinkToken(token);
    }
}

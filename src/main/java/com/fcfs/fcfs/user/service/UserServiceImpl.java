package com.fcfs.fcfs.user.service;

import com.fcfs.fcfs.email.service.EmailVerificationService;
import com.fcfs.fcfs.user.dto.request.UserSignUpRequestDto;
import com.fcfs.fcfs.user.dto.response.UserResponseDto;
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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    @Override
    public void registerUser(UserSignUpRequestDto requestDto) throws MessagingException, UnsupportedEncodingException {

        // Todo: 이메일 중복 검사, 닉네임 중복검사, 전화번호 중복검사

        // DB에 암호화 및 저장 진행
        String encodedPassword = passwordEncoder.encode(requestDto.password());
        User user = User.from(requestDto, encodedPassword);
        userRepository.save(user);

        // 이메일 전송
        emailVerificationService.sendVerificationEmail(user.getEmail());
    }

    @Override
    public UserResponseDto verifyEmail(String token) {

        Long userId = emailVerificationService.verifyLinkToken(token);
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다");
        }
        return UserResponseDto.toDto(user);
    }

    @Override
    public UserResponseDto infoUser(Long userId) {

        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        return UserResponseDto.toDto(user);
    }
}

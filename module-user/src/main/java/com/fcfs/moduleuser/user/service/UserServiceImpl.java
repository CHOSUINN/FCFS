package com.fcfs.moduleuser.user.service;

import com.fcfs.moduleuser.email.service.EmailVerificationService;
import com.fcfs.moduleuser.global.exception.CustomException;
import com.fcfs.moduleuser.global.exception.ErrorCode;
import com.fcfs.moduleuser.user.dto.request.UserSignUpRequestDto;
import com.fcfs.moduleuser.user.dto.response.UserEntityResponseDto;
import com.fcfs.moduleuser.user.dto.response.UserResponseDto;
import com.fcfs.moduleuser.user.entity.User;
import com.fcfs.moduleuser.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

@Slf4j(topic = "UserServiceImpl")
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

    @Override
    public UserEntityResponseDto getUserEntity(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        return UserEntityResponseDto.toDto(user);
    }
}

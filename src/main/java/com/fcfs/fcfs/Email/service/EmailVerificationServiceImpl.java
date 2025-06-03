package com.fcfs.fcfs.Email.service;

import com.fcfs.fcfs.Email.repository.EmailTokenRepository;
import com.fcfs.fcfs.user.entity.User;
import com.fcfs.fcfs.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j(topic = "EmailVerificationService")
@Service
@RequiredArgsConstructor
@Transactional
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final UserRepository userRepository;
    private final EmailService emailServiceImpl;
    private final EmailTokenRepository emailTokenRepository;

    // 이메일 링크 유효시간 5분
    private static final Duration EMAIL_VERIFICATION_DURATION = Duration.ofMinutes(5);

    // 인증링크URL
    @Value("${app.base-url}")
    private String baseUrl;

    // 이메일 템플릿
    @Value("${app.email.verification-template}")
    private String verificationTemplate;

    // 이메일 제목
    @Value("${app.email.title}")
    private String verificationTitle;

    @Override
    public void sendVerificationEmail(String email) throws MessagingException, UnsupportedEncodingException {
        // 회원가입
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("회원가입 절차를 제대로 진행해주세요")
        ));

        if (user.get().getIsVerified()) {
            throw new IllegalArgumentException("이미 이메일 인증이 완료된 회원입니다.");
        }

        String token = UUID.randomUUID().toString();
        String userId = user.get().getId().toString();
        emailTokenRepository.saveToken(token, userId, EMAIL_VERIFICATION_DURATION);

        // 이메일 전송
        String verifyUrl = baseUrl + "/api/verify?token=" + token;

        String emailText = verificationTemplate
                .replace("[[USER_EMAIL]]", user.get().getEmail())
                .replace("[[VERIFY_URL]]", verifyUrl);

        emailServiceImpl.sendVerificationEmail(email, verificationTitle, emailText);
    }

    @Override
    public Long verifyLinkToken(String token) {

        // 1. redis에서 토큰에 매핑된 UserId 조회
        String userId = emailTokenRepository.getUserIdByToken(token);
        log.info("이메일 인증 회원 id찾기 : {}", userId);
        if (userId == null) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 링크입니다.");
        }

        // 2. User 엔티티 조회
        Optional<User> user = userRepository.findById(Long.parseLong(userId));

        // 3. 이미 인증된 상태라면 토큰만 지운다
        if (user.get().getIsVerified()) {
            emailTokenRepository.deleteToken(token);
            throw new IllegalArgumentException("이미 인증된 회원입니다.");
        }

        // 4. 회원의 isVerified 컬럼을 인증해주거나,
        user.get().setIsVerified(true);
        userRepository.save(user.get());

        // 5. redis 토큰 삭제
        emailTokenRepository.deleteToken(token);

        return user.get().getId();
    }
}

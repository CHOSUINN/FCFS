package com.fcfs.fcfs.Email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    // EmailConfig가 주입됨
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    // 보내려는 이메일, 제목, 본문만 입력하면 무슨 이메일이든 보낼 수 있게 설정해두었다.
    @Override
    public void sendVerificationEmail(String toEmail,
                                      String title,
                                      String htmlContent) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                "UTF-8"
        );

        String encodedTitle = MimeUtility.encodeText(title, "UTF-8", "B");
        helper.setTo(toEmail);
        helper.setSubject(encodedTitle);
        helper.setFrom(fromAddress);
        helper.setText(htmlContent, true); // 두 번째 파라미터를 `true`로 하면 HTML로 전송됩니다.

        // 3) 실제 전송
        mailSender.send(message);
    }
}

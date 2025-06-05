package com.fcfs.moduleorder.email.service;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface EmailService {

    // 보내려는 이메일, 제목, 본문만 입력하면 무슨 이메일이든 보낼 수 있게 설정해두었다.
    void sendVerificationEmail(String toEmail,
                               String title,
                               String htmlContent) throws MessagingException, UnsupportedEncodingException;
}

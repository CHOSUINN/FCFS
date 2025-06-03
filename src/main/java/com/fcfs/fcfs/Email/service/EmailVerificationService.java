package com.fcfs.fcfs.Email.service;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface EmailVerificationService {

    void sendVerificationEmail(String email) throws MessagingException, UnsupportedEncodingException;

    void verifyLinkToken(String token);
}

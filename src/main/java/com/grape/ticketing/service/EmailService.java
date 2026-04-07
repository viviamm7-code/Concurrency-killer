package com.grape.ticketing.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;

    private String createCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
    @Async
    public void sendVerificationEmail(String toEmail) {
        String authCode = createCode();

        // Redis에 "email:인증번호" 형태로 3분 동안 저장
        redisTemplate.opsForValue().set(toEmail, authCode, 3, TimeUnit.MINUTES);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("포도알 사수 작전 회원가입 인증 번호입니다.");

            // HTML 형식으로 메일 본문 작성
            String content = "<h3>안녕하세요, 회원가입을 위한 인증 번호입니다.</h3>" +
                    "<h1>" + authCode + "</h1>" +
                    "<p>3분 이내에 인증 번호를 입력창에 입력해 주세요.</p>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }
    }
    public boolean verifyCode(String email, String userInputCode) {
        String saveCode = redisTemplate.opsForValue().get(email);
        return userInputCode.equals(saveCode);
    }
}
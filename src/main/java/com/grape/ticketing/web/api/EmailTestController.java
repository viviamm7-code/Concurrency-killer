package com.grape.ticketing.web.api;

import com.grape.ticketing.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmailTestController {

    private final EmailService emailService;

    //인증 코드 발송 API
    @PostMapping("/email-send")
    public String sendEmail(@RequestParam("email") String email) {
        emailService.sendVerificationEmail(email);
        return "인증 번호가 발송되었습니다.";
    }

    // 인증 코드 검증 API
    @PostMapping("/email-verify")
    public boolean verifyEmail(@RequestParam("email") String email, @RequestParam("code") String code) {
        return emailService.verifyCode(email, code);
    }
}

package com.grape.ticketing.web.api;

import com.grape.ticketing.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Tag(name = "회원가입 이메일 API")
public class EmailTestController {

    private final EmailService emailService;

    //인증 코드 발송 API
    @Operation(summary = "이메일 발송")
    @PostMapping("/email-send")
    public String sendEmail(@RequestParam("email") String email) {
        emailService.sendVerificationEmail(email);
        return "인증 번호가 발송되었습니다.";
    }

    // 인증 코드 검증 API
    @Operation(summary = "이메일 검증")
    @PostMapping("/email-verify")
    public boolean verifyEmail(@RequestParam("email") String email, @RequestParam("code") String code) {
        return emailService.verifyCode(email, code);
    }
}

package com.grape.ticketing.web.api;

import com.grape.ticketing.domain.member.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthApiController {

    @GetMapping("/api/me")
    public MeResponse getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }

        return new MeResponse(
                userDetails.getMemberId(),
                userDetails.getUsername(),
                userDetails.getRole()
        );
    }

    @Getter
    @AllArgsConstructor
    static class MeResponse {
        private Long memberId;
        private String loginId;
        private String role;
    }
}
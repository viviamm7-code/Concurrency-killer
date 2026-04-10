package com.grape.ticketing.web.api;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.repository.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@Tag(name = "사용자 확인 API")
public class AuthApiController {

    private final MemberRepository memberRepository;

    @Operation(summary = "현재 로그인한 사용자 정보를 조회하는 인증 확인")
    @GetMapping("api/me")
    public MeResponse getMe(HttpSession session) {
        Object loginMember = session.getAttribute("loginMember");

        if (loginMember == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        Long memberId = Long.valueOf(String.valueOf(loginMember));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));

        return new MeResponse(
                member.getId(),
                member.getUsername(),
                member.getRole()
        );
    }

    @Getter
    @AllArgsConstructor
    static class MeResponse {
        private Long memberId;
        private String username;
        private String role;
    }
}
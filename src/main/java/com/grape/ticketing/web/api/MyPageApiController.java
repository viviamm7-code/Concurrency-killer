package com.grape.ticketing.web.api;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.dto.SessionMemberDto;
import com.grape.ticketing.dto.user.MemberDeleteRequestDto;
import com.grape.ticketing.dto.user.MyPageResponseDto;
import com.grape.ticketing.dto.user.PasswordUpdateRequestDto;
import com.grape.ticketing.repository.MemberRepository;
import com.grape.ticketing.service.AdminMemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageApiController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminMemberService adminMemberService;

    @GetMapping
    public MyPageResponseDto getMyPage(HttpSession session) {
        Member member = getLoginMember(session);

        String createdAt = member.getCreatedDate() == null
                ? "-"
                : member.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        return new MyPageResponseDto(
                member.getId(),
                member.getUsername(),
                member.getName(),
                member.getEmail(),
                member.getRole(),
                createdAt
        );
    }

    @PatchMapping("/password")
    public Map<String, String> updatePassword(@RequestBody PasswordUpdateRequestDto request, HttpSession session) {
        Member member = getLoginMember(session);

        if (request.getCurrentPassword() == null || request.getCurrentPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "현재 비밀번호를 입력해주세요.");
        }

        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "새 비밀번호를 입력해주세요.");
        }

        if (request.getNewPasswordConfirm() == null || request.getNewPasswordConfirm().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "새 비밀번호 확인을 입력해주세요.");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "현재 비밀번호가 올바르지 않습니다.");
        }

        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        if (request.getNewPassword().length() < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "새 비밀번호는 4자 이상이어야 합니다.");
        }

        member.setPassword(passwordEncoder.encode(request.getNewPassword()));
        memberRepository.save(member);

        return Map.of("message", "비밀번호가 변경되었습니다.");
    }

    private Member getLoginMember(HttpSession session) {
        Object loginMember = session.getAttribute("loginMember");

        if (loginMember == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        Long memberId;
        try {
            memberId = Long.valueOf(String.valueOf(loginMember));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "세션 정보가 올바르지 않습니다.");
        }

        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));
    }

    @DeleteMapping("/delete")
    public void deleteMyAccount(HttpSession session) {
        Long loginMemberId = (Long) session.getAttribute("loginMember");

        if (loginMemberId == null) {
            throw new IllegalArgumentException("로그인된 사용자가 없습니다.");
        }

        adminMemberService.deleteMember(loginMemberId);
        session.invalidate();
    }
}
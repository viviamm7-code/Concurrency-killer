package com.grape.ticketing.web;

import com.grape.ticketing.dto.member.MemberTO;
import com.grape.ticketing.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.grape.ticketing.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Tag(name = "로그인 API")
public class MemberController {

    private final LoginService loginService;
    private final MemberService memberService;


    @GetMapping("/login")
    // @ResponseBody
    public Object login(Authentication authentication) {
        // 이미 로그인 된 사용자인지 확인
        boolean isLoggedIn = authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);

        if (isLoggedIn) {
            String script = "<script>" +
                    "alert('이미 로그인 된 상태입니다.');"+
                    "location.href = 'performance-list';" +
                    "</script>";
            return ResponseEntity.ok()
                    .header("Content-Type", "text/html;charset=UTF-8")
                    .body(script);
        }

        return "members/login";
    }
    @GetMapping("/already-logged-in")
    @ResponseBody
    public ResponseEntity<String> alreadyLoggedIn() {
        String script = "<script>" +
                "alert('이미 로그인 된 상태입니다.');" +
                "location.href = '/performance-list';" +
                "</script>";
        return ResponseEntity.ok()
                .header("content-type", "text/html; charset=utf-8")
                .body(script);
    }
    @RequestMapping("/login")
    public String login(){
        return "members/login";
    }
    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/join")
    public String join(){
        return "members/join";
    }

    @PostMapping("/api/member/join")
    public ResponseEntity<String> join(@Valid @RequestBody MemberTO memberTO) {
        System.out.println("===> 회원가입 요청 수신: " + memberTO.getUsername());
        memberService.join(memberTO);
        System.out.println("===> 서비스 로직 통과 완료!");
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @RequestMapping("/ticketing")
    public String ticketing(){
        return "ticketing";
    }

    @Operation(summary = "세션 키값으로 로그인 상태 확인")
    @GetMapping("/api/auth/status")
    @ResponseBody
    public Map<String, Object> getAuthStatus(HttpSession session) {
        Map<String, Object> status = new HashMap<>();

        // 저장한 세션 키 값인 "loginMember"를 확인합니다.
        Object loginMemberId = session.getAttribute("loginMember");

        if (loginMemberId != null) {
            status.put("isLoggedIn", true);
            status.put("memberId", loginMemberId);
        } else {
            status.put("isLoggedIn", false);
            status.put("memberId", null);
        }
        return status;
    }

    @Operation(summary = "로그인 상태 확인")
    @GetMapping("/api/auth/check")
    @ResponseBody
    public boolean checkLogin(HttpSession session) {
        return session.getAttribute("loginMember") != null;
    }
}

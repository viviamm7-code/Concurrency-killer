package com.grape.ticketing.web;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.dto.member.MemberTO;
import com.grape.ticketing.service.LoginService;
import com.grape.ticketing.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
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
/*    @RequestMapping("/login")
    public String login(){
        return "members/login";
    }*/
    @RequestMapping("/logout1")
    public String logout1(HttpSession session){
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

    @GetMapping("/api/auth/status")
    @ResponseBody
    public Map<String, Object> getAuthStatus(Authentication authentication) {
        Map<String, Object> status = new HashMap<>();

        boolean isLoggedIn =
                authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);


        if (isLoggedIn) {
            status.put("isLoggedIn", true);
            status.put("memberId", authentication.getName());
        } else {
            status.put("isLoggedIn", false);
            status.put("memberId", null);
        }
        return status;
    }

    @GetMapping("/api/auth/check")
    @ResponseBody
    public Map<String, Object> checkLogin(Authentication authentication) {
        Map<String, Object> status = new HashMap<>();

        // 시큐리티 인증 객체가 있고, 익명 사용자인지 확인
        boolean isLoggedIn = authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
        status.put("isLoggedIn", isLoggedIn);
        return status;
    }
/*    public boolean checkLogin(HttpSession session) {
        return session.getAttribute("loginMember") != null;
    }*/
}

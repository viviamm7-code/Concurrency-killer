package com.grape.ticketing.web;

import com.grape.ticketing.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Tag(name = "로그인 API")
public class MemberController {

    private final LoginService loginService;

    @RequestMapping("/login")
    public String login(){
        return "members/login";
    }
    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/login";
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

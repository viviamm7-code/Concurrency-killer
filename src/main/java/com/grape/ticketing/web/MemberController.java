package com.grape.ticketing.web;

import com.grape.ticketing.domain.Member;
import com.grape.ticketing.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final LoginService loginService;

    @RequestMapping("/login")
    public String login(){
        return "login";
    }
    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/members/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpServletRequest request,
                        HttpSession session) {
        Member loginMember = loginService.login(username, password);

        if(loginMember == null) {
            return "redirect:/login?error=true";
        }

        session = request.getSession();
        session.setAttribute("loginMember", loginMember.getId());

        return "redirect:/performance-list";
    }

    @RequestMapping("/ticketing")
    public String ticketing(){
        return "ticketing";
    }

    @GetMapping("/api/auth/status")
    @ResponseBody
    public Map<String, Object> getAuthStatus(HttpSession session) {
        Map<String, Object> status = new HashMap<>();

        // 저장한 세션 키 값인 "loginMember"를 확인합니다.
        Object loginMemberId = session.getAttribute("loginMember");

        if (loginMemberId != null) {
            status.put("isLoggedIn", true);
        } else {
            status.put("isLoggedIn", false);
        }
        return status;
    }
}

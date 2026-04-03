package com.grape.ticketing.web;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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

    @RequestMapping("/login")
    public String login(){
        return "members/login";
    }
    @RequestMapping("/logout1")
    public String logout1(HttpSession session){
        session.invalidate();
        return "redirect:/login";
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
    public boolean checkLogin(HttpSession session) {
        return session.getAttribute("loginMember") != null;
    }
}

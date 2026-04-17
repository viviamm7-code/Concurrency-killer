package com.grape.ticketing.web.nav;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class MyPageController {

    @GetMapping
    public String myPage() {
        return "user/mypage";
    }
}
package com.grape.ticketing.web.nav;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("admin")
public class AdminController {


    @GetMapping
    public String adminHome() {
        return "admin/home";
    }

    @GetMapping("/members")
    public String members() {
        return "admin/members";
    }

    @GetMapping("/reservations")
    public String reservations() {
        return "admin/reservations";
    }

    @GetMapping("/payments")
    public String payments() {
        return "admin/payments";
    }
}
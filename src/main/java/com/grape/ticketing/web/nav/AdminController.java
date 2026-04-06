package com.grape.ticketing.web.nav;

import com.grape.ticketing.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public String adminHome() {
        return "admin/home";
    }

    @GetMapping("/members")
    public String memberManagePage(Model model) {
        model.addAttribute("members", adminService.getMembers());
        return "admin/members";
    }


    @GetMapping("/performances")
    public String performances(Model model) {
        return "admin/performances";
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
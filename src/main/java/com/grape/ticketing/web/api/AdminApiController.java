package com.grape.ticketing.web.api;

import com.grape.ticketing.dto.admin.*;
import com.grape.ticketing.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public AdminDashboardResponseDto getDashboard() {
        return adminService.getDashboard();
    }

    @GetMapping("/members")
    public List<AdminMemberResponseDto> getMembers() {
        return adminService.getMembers();
    }

    @GetMapping("/performances")
    public List<AdminPerformanceResponseDto> getPerformances() {
        return adminService.getPerformances();
    }

    @GetMapping("/reservations")
    public List<AdminReservationResponseDto> getReservations() {
        return adminService.getReservations();
    }

    @GetMapping("/payments")
    public List<AdminPaymentResponseDto> getPayments() {
        return adminService.getPayments();
    }
}
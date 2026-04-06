package com.grape.ticketing.web.api;

import com.grape.ticketing.dto.admin.*;
import com.grape.ticketing.service.AdminMemberService;
import com.grape.ticketing.service.AdminPaymentService;
import com.grape.ticketing.service.AdminReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AdminMemberService adminMemberService;
    private final AdminReservationService adminReservationService;
    private final AdminPaymentService adminPaymentService;

    @GetMapping()
    public AdminDashboardResponseDto dashboard() {
        return adminMemberService.getDashboard();
    }

    @GetMapping("/members")
    public List<AdminMemberListResponseDto> getMembers(
            @RequestParam(required = false) String keyword
    ) {
        return adminMemberService.getMembers(keyword);
    }

    @GetMapping("/members/{memberId}")
    public AdminMemberDetailResponseDto getMemberDetail(
            @PathVariable Long memberId
    ) {
        return adminMemberService.getMemberDetail(memberId);
    }

    @DeleteMapping("/members/{memberId}/delete")
    public void deleteMember(@PathVariable Long memberId) {
        adminMemberService.deleteMember(memberId);
    }

    @GetMapping("/reservations")
    public List<AdminReservationListResponseDto> getReservations(
            @RequestParam(required = false) String keyword
    ) {
        return adminReservationService.getReservations(keyword);
    }

    @GetMapping("/payments")
    public List<AdminPaymentListResponseDto> getPayments(
            @RequestParam(required = false) String keyword
    ) {
        return adminPaymentService.getPayments(keyword);
    }
}
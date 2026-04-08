package com.grape.ticketing.web.api;

import com.grape.ticketing.dto.admin.*;
import com.grape.ticketing.service.AdminMemberService;
import com.grape.ticketing.service.AdminPaymentService;
import com.grape.ticketing.service.AdminReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin")
@Tag(name = "관리자 페이지 API")
public class AdminApiController {

    private final AdminMemberService adminMemberService;
    private final AdminReservationService adminReservationService;
    private final AdminPaymentService adminPaymentService;

    @Operation(summary = "대시보드 조회")
    @GetMapping()
    public AdminDashboardResponseDto dashboard() {
        return adminMemberService.getDashboard();
    }

    @Operation(summary = "회원 목록 조회")
    @GetMapping("/members")
    public List<AdminMemberListResponseDto> getMembers(
            @RequestParam(required = false) String keyword
    ) {
        return adminMemberService.getMembers(keyword);
    }

    @Operation(summary = "회원 별 상세 정보 조회")
    @GetMapping("/members/{memberId}")
    public AdminMemberDetailResponseDto getMemberDetail(
            @PathVariable Long memberId
    ) {
        return adminMemberService.getMemberDetail(memberId);
    }

    @Operation(summary = "회원 별 삭제")
    @DeleteMapping("/members/{memberId}/delete")
    public void deleteMember(@PathVariable Long memberId) {
        adminMemberService.deleteMember(memberId);
    }

    @Operation(summary = "예약 목록 조회")
    @GetMapping("/reservations")
    public List<AdminReservationListResponseDto> getReservations(
            @RequestParam(required = false) String keyword
    ) {
        return adminReservationService.getReservations(keyword);
    }

    @Operation(summary = "결제 목록 조회")
    @GetMapping("/payments")
    public List<AdminPaymentListResponseDto> getPayments(
            @RequestParam(required = false) String keyword
    ) {
        return adminPaymentService.getPayments(keyword);
    }
}
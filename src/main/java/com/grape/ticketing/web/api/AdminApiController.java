package com.grape.ticketing.web.api;

import com.grape.ticketing.dto.admin.AdminDashboardResponseDto;
import com.grape.ticketing.dto.admin.AdminMemberDetailResponseDto;
import com.grape.ticketing.dto.admin.AdminMemberListResponseDto;
import com.grape.ticketing.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AdminMemberService adminMemberService;

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
}
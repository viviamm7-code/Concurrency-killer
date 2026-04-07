package com.grape.ticketing.service;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.dto.admin.AdminDashboardResponseDto;
import com.grape.ticketing.dto.admin.AdminMemberDetailResponseDto;
import com.grape.ticketing.dto.admin.AdminMemberListResponseDto;
import com.grape.ticketing.repository.MemberRepository;
import com.grape.ticketing.repository.PaymentRepository;
import com.grape.ticketing.repository.PerformanceRepository;
import com.grape.ticketing.repository.ReservationRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberService {

    private final MemberRepository memberRepository;
    private final PerformanceRepository performanceRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDashboardResponseDto getDashboard() {
        long totalMembers = memberRepository.count();
        long totalPerformances = performanceRepository.count();
        long totalReservations = reservationRepository.count();
        long totalPayments = paymentRepository.count();

        return new AdminDashboardResponseDto(
                totalMembers,
                totalPerformances,
                totalReservations,
                totalPayments
        );
    }

    public List<AdminMemberListResponseDto> getMembers(String keyword) {
        List<Member> members;

        if (keyword == null || keyword.trim().isEmpty()) {
            members = memberRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        } else {
            members = memberRepository.findByUsernameContainingIgnoreCaseOrderByIdAsc(keyword);
        }

        return members.stream()
                .map(member -> new AdminMemberListResponseDto(
                        member.getId(),
                        member.getUsername()
                ))
                .toList();
    }

    public AdminMemberDetailResponseDto getMemberDetail(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        return new AdminMemberDetailResponseDto(
                member.getId(),
                member.getUsername(),
                member.getName(),
                member.getEmail(),
                member.getRole(),
                member.getCreatedDate()
        );
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        if (member.getRole().equals("ROLE_ADMIN")) {
            throw new IllegalArgumentException("관리자는 삭제할 수 없습니다.");
        }

        memberRepository.delete(member);
    }
    
}
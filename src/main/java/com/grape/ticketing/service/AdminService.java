package com.grape.ticketing.service;

import com.grape.ticketing.dto.admin.*;
import com.grape.ticketing.repository.MemberRepository;
import com.grape.ticketing.repository.PaymentRepository;
import com.grape.ticketing.repository.PerformanceRepository;
import com.grape.ticketing.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final MemberRepository memberRepository;
    private final PerformanceRepository performanceRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public AdminDashboardResponseDto getDashboard() {
        return new AdminDashboardResponseDto(
                memberRepository.count(),
                performanceRepository.count(),
                reservationRepository.count(),
                paymentRepository.count()
        );
    }

    public List<AdminMemberResponseDto> getMembers() {
        return memberRepository.findAll().stream()
                .map(member -> new AdminMemberResponseDto(
                        member.getId(),
                        member.getUsername(),
                        member.getRole()
                ))
                .toList();
    }

    public List<AdminPerformanceResponseDto> getPerformances() {
        return performanceRepository.findAll().stream()
                .map(performance -> new AdminPerformanceResponseDto(
                        performance.getId(),
                        performance.getPerformanceName(),
                        performance.getVenue().getVenueName(),
                        String.valueOf(performance.getEndDate())
                ))
                .toList();
    }

    public List<AdminReservationResponseDto> getReservations() {
        return reservationRepository.findAll().stream()
                .map(reservation -> new AdminReservationResponseDto(
                        reservation.getId(),
                        reservation.getMember().getName(),
                        reservation.getPerformance().getPerformanceName(),
                        reservation.getReservationStatus().name(),
                        reservation.getPayment().getAmount()
                ))
                .toList();
    }

    public List<AdminPaymentResponseDto> getPayments() {
        return paymentRepository.findAll().stream()
                .map(payment -> new AdminPaymentResponseDto(
                        payment.getId(),
                        payment.getReservation().getId(),
                        payment.getOrderId(),
                        payment.getPaymentKey(),
                        payment.getAmount()
                ))
                .toList();
    }
}
package com.grape.ticketing.service;

import com.grape.ticketing.domain.*;
import com.grape.ticketing.dto.ReservationDetailDto;
import com.grape.ticketing.dto.ReservationDto;
import com.grape.ticketing.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public List<ReservationDto> getReservationList(Long memberId) {
        List<Reservation> reservations = reservationRepository.findReservationByMemberId(memberId);

        return reservations.stream()
                .filter(reservation -> !reservation.getReservationSeats().isEmpty()) // 좌석번호없는애는 안넣음
                .map(reservation -> new ReservationDto(
                        reservation.getId(),
                        reservation.getPerformance().getPerformanceName(),
                        reservation.getPerformance().getVenue(),
                        reservation.getPerformance().getStartedAt(),
                        reservation.getReservedAt(),
                        reservation.getReservationSeats().stream()
                                .map(reservationSeat -> reservationSeat.getSeat().getSeatNumber())
                                .toList(),
                        reservation.getPerformance().getPerformanceStatus().name()
                ))
                .toList();
    }

    public ReservationDetailDto getDetailReservation(Long memberId, Long reservationId) {
        Reservation reservation = reservationRepository
                .findByMemberIdAndId(memberId, reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예매 내역이 없습니다."));

        return new ReservationDetailDto(
                reservation.getId(),
                reservation.getMember().getUsername(),
                reservation.getPerformance().getPerformanceName(),
                reservation.getPerformance().getVenue(),
                reservation.getPerformance().getStartedAt(),
                reservation.getReservedAt(),
                reservation.getReservationSeats().stream()
                        .map(reservationSeat -> reservationSeat.getSeat().getSeatNumber())
                        .toList(),
                reservation.getPerformance().getPrice() * reservation.getReservationSeats().size(),
                reservation.getPerformance().getPerformanceStatus().name()
        );
    }
}

package com.grape.ticketing.service;

import com.grape.ticketing.domain.*;
import com.grape.ticketing.domain.status.ReservationStatus;
import com.grape.ticketing.domain.status.SeatStatus;
import com.grape.ticketing.dto.*;
import com.grape.ticketing.dto.CancelPolicyResultDto;
import com.grape.ticketing.mapper.ReservationCancelMapper;
import com.grape.ticketing.mapper.ReservationMapper;
import com.grape.ticketing.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final ReservationCancelMapper reservationCancelMapper;
    private final ReservationCancelPolicyService reservationCancelPolicyService;
    private final SeatRepository seatRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final MemberRepository memberRepository;
    private final PerformanceRepository performanceRepository;

    public List<ReservationDto> getReservationList(Long memberId) {
        List<Reservation> reservations = reservationRepository.findReservationByMemberIdOrderByReservedAtDesc(memberId);

        return reservations.stream()
                .map(reservationMapper::toReservationDto)
                .toList();
    }

    public ReservationDetailDto getDetailReservation(Long memberId, Long reservationId) {
        Reservation reservation = reservationRepository
                .findByMemberIdAndId(memberId, reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예매 내역이 없습니다."));

        return reservationMapper.toReservationDetailDto(reservation);
    }

    @Transactional
    public ReservationCancelDto cancelReservation(Long memberId, Long reservationId) {
        Reservation reservation = reservationRepository.findByMemberIdAndId(memberId, reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예매 내역이 없습니다."));

        CancelPolicyResultDto policyResult = reservationCancelPolicyService.calculate(reservation);

        if (!policyResult.isCancelable()) {
            throw new IllegalStateException(policyResult.getMessage());
        }

        List<Seat> seats = reservation.getReservationSeats().stream()
                .map(ReservationSeat::getSeat)
                .toList();

        for (Seat seat : seats) {
            seat.setSeatStatus(SeatStatus.AVAILABLE);
        }

        seatRepository.saveAll(seats);

        reservation.setReservationStatus(ReservationStatus.CANCELED);
        reservationRepository.save(reservation);

        return reservationCancelMapper.toReservationCancelDto(reservation, policyResult);
    }

    public ReservationCancelDto getCancelPreview(Long memberId, Long reservationId) {
        Reservation reservation = reservationRepository.findByMemberIdAndId(memberId, reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예매 내역이 없습니다."));

        CancelPolicyResultDto policyResult = reservationCancelPolicyService.calculate(reservation);

        return reservationCancelMapper.toReservationCancelDto(reservation, policyResult);
    }

    @Transactional
    public Long confirmReservation(ReservationConfirmRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연입니다."));

        Reservation reservation = reservationMapper.toReservation(member, performance);
        reservationRepository.save(reservation);

        List<Seat> seats = seatRepository.findAllByPerformanceIdAndSeatNumberIn(
                request.getPerformanceId(),
                request.getSeatNumbers()
        );

        if (seats.size() != request.getSeatNumbers().size()) {
            throw new IllegalArgumentException("존재하지 않거나 잘못된 좌석이 포함되어 있습니다.");
        }

        for (Seat seat : seats) {
            if (seat.getSeatStatus() == SeatStatus.RESERVED) {
                throw new IllegalStateException("이미 예약된 좌석입니다.");
            }

            seat.setSeatStatus(SeatStatus.RESERVED);

            ReservationSeat reservationSeat = reservationMapper.toReservationSeat(reservation, seat);
            reservationSeatRepository.save(reservationSeat);
        }

        return reservation.getId();
    }
}

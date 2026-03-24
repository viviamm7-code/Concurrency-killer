package com.grape.ticketing.service;

import com.grape.ticketing.domain.ReservationSeat;
import com.grape.ticketing.repository.ReservationRepository;
import com.grape.ticketing.repository.ReservationSeatRepository;
import jakarta.xml.bind.SchemaOutputResolver;
import com.grape.ticketing.domain.Reservation;
import com.grape.ticketing.domain.ReservationSeat;
import com.grape.ticketing.domain.Seat;
import com.grape.ticketing.domain.status.ReservationStatus;
import com.grape.ticketing.domain.status.SeatStatus;
import com.grape.ticketing.dto.CancelPolicyResultDto;
import com.grape.ticketing.dto.CancelPolicyResultDto;
import com.grape.ticketing.dto.ReservationCancelDto;
import com.grape.ticketing.dto.ReservationDetailDto;
import com.grape.ticketing.dto.ReservationDto;
import com.grape.ticketing.mapper.ReservationCancelMapper;
import com.grape.ticketing.mapper.ReservationMapper;
import com.grape.ticketing.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    private static final long MAX_SEATS_PER_MEMBER = 4;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final ReservationMapper reservationMapper;
    private final ReservationCancelMapper reservationCancelMapper;
    private final ReservationCancelPolicyService reservationCancelPolicyService;
    private final SeatRepository seatRepository;

    public long getReservedSeatCount(Long memberId, Long performanceId) {
        long reservedSeatCnt = reservationSeatRepository.countReservedSeatsByMemberIdAndPerformanceId(memberId, performanceId);
        
        return reservedSeatCnt;
    }

    public long getRemainingSeatCount(Long memberId, Long performanceId) {
        long reservedSeatCount = getReservedSeatCount(memberId, performanceId);
        long remainingSeatCount = MAX_SEATS_PER_MEMBER - reservedSeatCount;
        // 시스템상 오류로 잔여 수가 마이너스일 것을 대비해서 max 함수 사용
        return Math.max(remainingSeatCount, 0);
    }

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
}

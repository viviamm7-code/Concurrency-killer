package com.grape.ticketing.service;

import com.grape.ticketing.domain.Reservation;
import com.grape.ticketing.domain.ReservationSeat;
import com.grape.ticketing.domain.Seat;
import com.grape.ticketing.domain.status.ReservationStatus;
import com.grape.ticketing.domain.status.SeatStatus;
import com.grape.ticketing.repository.ReservationRepository;
import com.grape.ticketing.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationSchedulerService {
    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    @Scheduled(fixedDelay = 60000) // 1분마다 실행
    @Transactional
    public void completeExpiredReservations() {
        List<Reservation> reservations =
                reservationRepository.findAllByReservationStatusAndPerformance_StartedAtBefore(
                        ReservationStatus.RESERVED,
                        LocalDateTime.now()
                );

        for (Reservation reservation : reservations) {
            List<Seat> seats = reservation.getReservationSeats().stream()
                    .map(ReservationSeat::getSeat)
                    .toList();

            for (Seat seat : seats) {
                seat.setSeatStatus(SeatStatus.AVAILABLE);
            }

            reservation.setReservationStatus(ReservationStatus.COMPLETED);
        }

        seatRepository.saveAll(
                reservations.stream()
                        .flatMap(r -> r.getReservationSeats().stream())
                        .map(ReservationSeat::getSeat)
                        .toList()
        );
    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void completeReservations() {
        reservationService.completeExpiredReservations();
    }
}
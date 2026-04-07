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
    

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void completeReservations() {
        reservationService.completeExpiredReservations();
    }
}
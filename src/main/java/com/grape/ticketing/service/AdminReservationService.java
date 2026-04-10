package com.grape.ticketing.service;

import com.grape.ticketing.domain.Reservation;
import com.grape.ticketing.dto.admin.AdminReservationListResponseDto;
import com.grape.ticketing.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReservationService {

    private final ReservationRepository reservationRepository;

    public List<AdminReservationListResponseDto> getReservations(String keyword) {

        List<Reservation> reservations;

        if (keyword == null || keyword.trim().isEmpty()) {
            reservations = reservationRepository.findAll(
                    Sort.by(Sort.Direction.ASC, "id")
            );
        } else {
            reservations = reservationRepository
                    .findByMemberUsernameContainingIgnoreCase(
                            keyword,
                            Sort.by(Sort.Direction.ASC, "id")
                    );
        }

        return reservations.stream()
                .map(this::toDto)
                .toList();
    }

    private AdminReservationListResponseDto toDto(Reservation r) {

        String seatInfo = r.getReservationSeats().stream()
                .map(seat -> seat.getSeat().getSeatNumber())
                .reduce((a, b) -> a + ", " + b)
                .orElse("-");

        return new AdminReservationListResponseDto(
                r.getId(),
                r.getMember().getUsername(),
                r.getMember().getName(),
                r.getPerformance().getPerformanceName(),
                seatInfo,
                r.getReservationStatus().name()
        );
    }
}

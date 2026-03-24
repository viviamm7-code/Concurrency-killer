package com.grape.ticketing.mapper;

import com.grape.ticketing.domain.Reservation;
import com.grape.ticketing.dto.reservation.CancelPolicyResultDto;
import com.grape.ticketing.dto.reservation.ReservationCancelDto;
import org.springframework.stereotype.Component;

@Component
public class ReservationCancelMapper {

    public ReservationCancelDto toReservationCancelDto(Reservation reservation, CancelPolicyResultDto result) {
        return new ReservationCancelDto(
                reservation.getId(),
                reservation.getPerformance().getPerformanceName(),
                result.getTotalPrice(),
                result.getRefundAmount(),
                result.getRefundRate(),
                result.getMessage(),
                reservation.getReservationStatus().name()
        );
    }
}
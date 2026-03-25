package com.grape.ticketing.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationConfirmResponse {
    private Long reservationId;
    private String message;
}

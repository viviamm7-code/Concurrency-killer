package com.grape.ticketing.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationCancelDto {
    private Long reservationId;
    private String performanceName;
    private int totalPrice;
    private int refundAmount;
    private int refundRate;
    private String message;
    private String reservationStatus;
}

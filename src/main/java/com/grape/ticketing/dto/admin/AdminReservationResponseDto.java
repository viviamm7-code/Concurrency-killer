package com.grape.ticketing.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminReservationResponseDto {
    private Long reservationId;
    private String loginId;
    private String performanceName;
    private String reservationStatus;
    private Long totalPrice;
}
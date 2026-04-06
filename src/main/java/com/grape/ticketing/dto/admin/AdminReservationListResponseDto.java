package com.grape.ticketing.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminReservationListResponseDto {
    private Long reservationId;
    private String username;
    private String memberName;
    private String performanceTitle;
    private String seatInfo;
    private String reservationStatus;
}
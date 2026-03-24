package com.grape.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReservationDetailDto {
    private Long reservationId;
    private String reservationName;
    private String performanceName;
    private String venue;
    private LocalDateTime startedAt;
    private LocalDateTime reservedAt;
    private List<String> seatNumbers;
    private int price;
    private String reservationStatus;
}
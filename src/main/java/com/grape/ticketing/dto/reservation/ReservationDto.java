package com.grape.ticketing.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReservationDto {
    private Long reservationId;
    private String performanceName;
    private String venue;
    private LocalDateTime startedAt;
    private Date reservedDate;
    private LocalDateTime reservedAt;
    private List<String> seatNumbers;
    private String reservationStatus;
    private String performanceStatus;
    private String imageUrl;
}
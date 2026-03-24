package com.grape.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@Getter
@AllArgsConstructor
public class ReservationConfirmRequest {
    private Long memberId;
    private Long performanceId;
    private List<String> seatNumbers;
}
package com.grape.ticketing.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class SeatHoldConflictException extends RuntimeException {
    private final List<String> conflictedSeats;

    public SeatHoldConflictException(List<String> conflictedSeats) {
        super("다른 사용자가 이미 선택한 좌석이 있습니다.");
        this.conflictedSeats = conflictedSeats;
    }
}

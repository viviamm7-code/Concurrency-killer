package com.grape.ticketing.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CancelPolicyResultDto {
    private final int totalPrice;
    private final int refundAmount;
    private final int refundRate;
    private final String message;
    private final boolean cancelable;
}
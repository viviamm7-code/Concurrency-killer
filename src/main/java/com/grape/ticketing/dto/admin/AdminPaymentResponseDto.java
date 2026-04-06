package com.grape.ticketing.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminPaymentResponseDto {
    private Long paymentId;
    private Long reservationId;
    private String orderId;
    private String paymentKey;
    private Long amount;
}
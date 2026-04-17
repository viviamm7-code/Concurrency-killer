package com.grape.ticketing.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminPaymentListResponseDto {

    private Long paymentId;
    private String username;
    private String memberName;
    private String performanceTitle;
    private Long amount;
    private String orderId;
    private LocalDateTime paidAt;
}
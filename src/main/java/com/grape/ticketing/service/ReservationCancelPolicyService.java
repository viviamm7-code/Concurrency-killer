package com.grape.ticketing.service;

import com.grape.ticketing.domain.Reservation;
import com.grape.ticketing.dto.reservation.CancelPolicyResultDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReservationCancelPolicyService {

    public CancelPolicyResultDto calculate(Reservation reservation) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startedAt = reservation.getPerformance().getStartedAt();

        int totalPrice = reservation.getPerformance().getPrice() * reservation.getReservationSeats().size();
        int refundRate;
        int refundAmount;
        String message;
        boolean cancelable;

        if (!now.isBefore(startedAt.minusDays(1))) {
            refundRate = 0;
            refundAmount = 0;
            message = "공연 1일 전 이후에는 취소할 수 없습니다.";
            cancelable = false;
        } else if (now.isBefore(startedAt.minusDays(7))) {
            refundRate = 100;
            refundAmount = totalPrice;
            message = "공연 7일 전까지는 전액 환불됩니다.";
            cancelable = true;
        } else if (now.isBefore(startedAt.minusDays(3))) {
            refundRate = 70;
            refundAmount = (int) (totalPrice * 0.7);
            message = "공연 3일 전까지는 70% 환불됩니다.";
            cancelable = true;
        } else {
            refundRate = 50;
            refundAmount = (int) (totalPrice * 0.5);
            message = "공연 1일 전까지는 50% 환불됩니다.";
            cancelable = true;
        }

        return new CancelPolicyResultDto(
                totalPrice,
                refundAmount,
                refundRate,
                message,
                cancelable
        );
    }
}
package com.grape.ticketing.service;

import com.grape.ticketing.domain.Reservation;
import com.grape.ticketing.dto.reservation.CancelPolicyResultDto;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;

@Service
public class ReservationCancelPolicyService {

    public CancelPolicyResultDto calculate(Reservation reservation) {
        LocalDateTime now = LocalDateTime.now();

        Date reservedDate = reservation.getReservedDate();
        String startedAt = reservation.getStartedAt();

        int totalPrice = reservation.getPerformance().getPrice() * reservation.getReservationSeats().size();

        if (reservedDate == null || startedAt == null || startedAt.isBlank()) {
            return new CancelPolicyResultDto(
                    totalPrice,
                    0,
                    0,
                    "공연 날짜 또는 시간이 없어 취소 정책을 계산할 수 없습니다.",
                    false
            );
        }

        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(reservedDate);
        LocalDate performanceDate = LocalDate.parse(dateString);

        LocalTime performanceTime = LocalDateTime.parse(startedAt).toLocalTime();

        LocalDateTime performanceDateTime = LocalDateTime.of(performanceDate, performanceTime);

        int refundRate;
        int refundAmount;
        String message;
        boolean cancelable;

        if (!now.isBefore(performanceDateTime.minusDays(1))) {
            refundRate = 0;
            refundAmount = 0;
            message = "공연 1일 전 이후에는 취소할 수 없습니다.";
            cancelable = false;
        } else if (now.isBefore(performanceDateTime.minusDays(7))) {
            refundRate = 100;
            refundAmount = totalPrice;
            message = "공연 7일 전까지는 전액 환불됩니다.";
            cancelable = true;
        } else if (now.isBefore(performanceDateTime.minusDays(3))) {
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
package com.grape.ticketing.service;

import com.grape.ticketing.domain.Payment;
import com.grape.ticketing.domain.Reservation;
import com.grape.ticketing.repository.PaymentRepository;
import com.grape.ticketing.repository.ReservationRepository;
import com.grape.ticketing.repository.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void savePayment(
            Long reservationId,
            String paymentKey,
            String orderId,
            Long amount
    ) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예매 정보가 없습니다."));

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setPaymentKey(paymentKey);
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setPaymentAt(LocalDateTime.now());

        paymentRepository.save(payment);
    }
}

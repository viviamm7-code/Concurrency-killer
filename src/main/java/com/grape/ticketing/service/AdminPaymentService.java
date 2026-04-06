package com.grape.ticketing.service;

import com.grape.ticketing.domain.Payment;
import com.grape.ticketing.dto.admin.AdminPaymentListResponseDto;
import com.grape.ticketing.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPaymentService {

    private final PaymentRepository paymentRepository;

    public List<AdminPaymentListResponseDto> getPayments(String keyword) {

        List<Payment> payments;

        if (keyword == null || keyword.trim().isEmpty()) {
            payments = paymentRepository.findAll(
                    Sort.by(Sort.Direction.ASC, "id")
            );
        } else {
            payments = paymentRepository
                    .findByReservationMemberUsernameContainingIgnoreCase(
                            keyword,
                            Sort.by(Sort.Direction.ASC, "id")
                    );
        }

        return payments.stream()
                .map(this::toDto)
                .toList();
    }

    private AdminPaymentListResponseDto toDto(Payment payment) {
        return new AdminPaymentListResponseDto(
                payment.getId(),
                payment.getReservation().getMember().getUsername(),
                payment.getReservation().getMember().getName(),
                payment.getReservation().getPerformance().getPerformanceName(),
                payment.getAmount(),
                payment.getOrderId(),
                payment.getCreatedDate()
        );
    }
}

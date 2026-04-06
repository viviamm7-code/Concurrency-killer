package com.grape.ticketing.repository;

import com.grape.ticketing.domain.Payment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    List<Payment> findByReservationMemberUsernameContainingIgnoreCase(
            String username,
            Sort sort
    );
}

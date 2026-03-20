package com.grape.ticketing.repository;

import com.grape.ticketing.domain.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
    void deleteAllByReservationId(Long reservationId);
}

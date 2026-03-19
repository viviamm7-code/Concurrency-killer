package com.grape.ticketing.repository;

import com.grape.ticketing.domain.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

}

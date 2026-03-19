package com.grape.ticketing.repository;

import com.grape.ticketing.domain.Reservation;
import com.grape.ticketing.domain.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {

    List<Reservation> findReservationByMemberId(Long memberId);

    Optional<Reservation> findByMemberIdAndId(Long memberId, Long reservationId);
}

package com.grape.ticketing.repository;

import com.grape.ticketing.domain.Reservation;
import com.grape.ticketing.domain.ReservationSeat;
import com.grape.ticketing.domain.status.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {

    List<Reservation> findReservationByMemberIdOrderByReservedAtDesc(Long memberId);

    Optional<Reservation> findByMemberIdAndId(Long memberId, Long reservationId);

    List<Reservation> findAllByReservationStatusAndPerformance_StartedAtBefore(
            ReservationStatus reservationStatus,
            LocalDateTime time
    );

    List<Reservation> findByReservationStatusAndPerformance_StartedAtBefore(
            ReservationStatus reservationStatus,
            LocalDateTime time
    );
}

package com.grape.ticketing.repository;

import com.grape.ticketing.domain.Reservation;
import com.grape.ticketing.domain.status.ReservationStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {

    List<Reservation> findReservationByMemberIdOrderByReservedAtDesc(Long memberId);

    Optional<Reservation> findByMemberIdAndId(Long memberId, Long reservationId);

    List<Reservation> findAllByReservationStatusAndReservedDateBefore(
            ReservationStatus reservationStatus,
            Date reservedDate
    );

    List<Reservation> findAll(Sort sort);

    List<Reservation> findByMemberUsernameContainingIgnoreCase(
            String username,
            Sort sort
    );
}

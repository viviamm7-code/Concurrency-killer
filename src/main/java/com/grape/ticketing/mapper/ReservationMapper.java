package com.grape.ticketing.mapper;

import com.grape.ticketing.domain.*;
import com.grape.ticketing.domain.status.ReservationStatus;
import com.grape.ticketing.dto.reservation.ReservationDetailDto;
import com.grape.ticketing.dto.reservation.ReservationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "reservationId", source = "id")
    @Mapping(target = "performanceName", source = "performance.performanceName")
    @Mapping(target = "venue", source = "performance.venue")
    @Mapping(target = "imageUrl", source = "performance.imageUrl")
    @Mapping(target = "startedAt", source = "performance.startedAt")
    @Mapping(target = "reservedAt", source = "reservedAt")
    @Mapping(target = "seatNumbers", expression = "java(toSeatNumbers(reservation.getReservationSeats()))")
    @Mapping(target = "reservationStatus", expression = "java(reservation.getReservationStatus().name())")
    @Mapping(target = "performanceStatus", expression = "java(reservation.getPerformance().getPerformanceStatus().name())")
    ReservationDto toReservationDto(Reservation reservation);

    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "performanceId", source = "performance.id")
    @Mapping(target = "reservationId", source = "id")
    @Mapping(target = "reservationName", source = "member.username")
    @Mapping(target = "performanceName", source = "performance.performanceName")
    @Mapping(target = "venue", source = "performance.venue")
    @Mapping(target = "startedAt", source = "performance.startedAt")
    @Mapping(target = "reservedAt", source = "reservedAt")
    @Mapping(target = "seatNumbers", expression = "java(toSeatNumbers(reservation.getReservationSeats()))")
    @Mapping(target = "price", expression = "java(calculateTotalPrice(reservation))")
    @Mapping(target = "imageUrl", source = "performance.imageUrl")
    @Mapping(target = "reservationStatus", expression = "java(reservation.getReservationStatus().name())")
    @Mapping(target = "performanceStatus", expression = "java(reservation.getPerformance().getPerformanceStatus().name())")
    ReservationDetailDto toReservationDetailDto(Reservation reservation);

    default List<String> toSeatNumbers(List<ReservationSeat> reservationSeats) {
        return reservationSeats.stream()
                .map(rs -> rs.getSeat().getSeatNumber())
                .toList();
    }

    default int calculateTotalPrice(Reservation reservation) {
        return reservation.getPerformance().getPrice() * reservation.getReservationSeats().size();
    }

    default Reservation toReservation(Member member, Performance performance) {
        Reservation reservation = new Reservation();
        reservation.setMember(member);
        reservation.setPerformance(performance);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setReservationStatus(ReservationStatus.RESERVED);
        return reservation;
    }

    default ReservationSeat toReservationSeat(Reservation reservation, Seat seat) {
        ReservationSeat reservationSeat = new ReservationSeat();
        reservationSeat.setReservation(reservation);
        reservationSeat.setSeat(seat);
        return reservationSeat;
    }
}
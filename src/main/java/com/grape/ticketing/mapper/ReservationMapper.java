package com.grape.ticketing.mapper;

import com.grape.ticketing.domain.Reservation;
import com.grape.ticketing.domain.ReservationSeat;
import com.grape.ticketing.dto.ReservationCancelDto;
import com.grape.ticketing.dto.ReservationDetailDto;
import com.grape.ticketing.dto.ReservationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "reservationId", source = "id")
    @Mapping(target = "performanceName", source = "performance.performanceName")
    @Mapping(target = "venue", source = "performance.venue")
    @Mapping(target = "startedAt", source = "performance.startedAt")
    @Mapping(target = "reservedAt", source = "reservedAt")
    @Mapping(target = "seatNumbers", expression = "java(toSeatNumbers(reservation.getReservationSeats()))")
    @Mapping(target = "reservationStatus", expression = "java(reservation.getReservationStatus().name())")
    @Mapping(target = "performanceStatus", expression = "java(reservation.getPerformance().getPerformanceStatus().name())")
    ReservationDto toReservationDto(Reservation reservation);

    @Mapping(target = "reservationId", source = "id")
    @Mapping(target = "reservationName", source = "member.username")
    @Mapping(target = "performanceName", source = "performance.performanceName")
    @Mapping(target = "venue", source = "performance.venue")
    @Mapping(target = "startedAt", source = "performance.startedAt")
    @Mapping(target = "reservedAt", source = "reservedAt")
    @Mapping(target = "seatNumbers", expression = "java(toSeatNumbers(reservation.getReservationSeats()))")
    @Mapping(target = "price", expression = "java(calculateTotalPrice(reservation))")
    @Mapping(target = "reservationStatus", expression = "java(reservation.getPerformance().getPerformanceStatus().name())")
    ReservationDetailDto toReservationDetailDto(Reservation reservation);

    default List<String> toSeatNumbers(List<ReservationSeat> reservationSeats) {
        return reservationSeats.stream()
                .map(rs -> rs.getSeat().getSeatNumber())
                .toList();
    }

    default int calculateTotalPrice(Reservation reservation) {
        return reservation.getPerformance().getPrice() * reservation.getReservationSeats().size();
    }

}
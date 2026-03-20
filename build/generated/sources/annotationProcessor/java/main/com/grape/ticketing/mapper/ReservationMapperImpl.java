package com.grape.ticketing.mapper;

import com.grape.ticketing.domain.Member;
import com.grape.ticketing.domain.Performance;
import com.grape.ticketing.domain.Reservation;
import com.grape.ticketing.dto.ReservationDetailDto;
import com.grape.ticketing.dto.ReservationDto;
import java.time.LocalDateTime;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-20T15:20:17+0900",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 17.0.17 (Azul Systems, Inc.)"
)
@Component
public class ReservationMapperImpl implements ReservationMapper {

    @Override
    public ReservationDto toReservationDto(Reservation reservation) {
        if ( reservation == null ) {
            return null;
        }

        Long reservationId = null;
        String performanceName = null;
        String venue = null;
        LocalDateTime startedAt = null;
        LocalDateTime reservedAt = null;

        reservationId = reservation.getId();
        performanceName = reservationPerformancePerformanceName( reservation );
        venue = reservationPerformanceVenue( reservation );
        startedAt = reservationPerformanceStartedAt( reservation );
        reservedAt = reservation.getReservedAt();

        List<String> seatNumbers = toSeatNumbers(reservation.getReservationSeats());
        String reservationStatus = reservation.getReservationStatus().name();
        String performanceStatus = reservation.getPerformance().getPerformanceStatus().name();

        ReservationDto reservationDto = new ReservationDto( reservationId, performanceName, venue, startedAt, reservedAt, seatNumbers, reservationStatus, performanceStatus );

        return reservationDto;
    }

    @Override
    public ReservationDetailDto toReservationDetailDto(Reservation reservation) {
        if ( reservation == null ) {
            return null;
        }

        Long reservationId = null;
        String reservationName = null;
        String performanceName = null;
        String venue = null;
        LocalDateTime startedAt = null;
        LocalDateTime reservedAt = null;

        reservationId = reservation.getId();
        reservationName = reservationMemberUsername( reservation );
        performanceName = reservationPerformancePerformanceName( reservation );
        venue = reservationPerformanceVenue( reservation );
        startedAt = reservationPerformanceStartedAt( reservation );
        reservedAt = reservation.getReservedAt();

        List<String> seatNumbers = toSeatNumbers(reservation.getReservationSeats());
        int price = calculateTotalPrice(reservation);
        String reservationStatus = reservation.getPerformance().getPerformanceStatus().name();

        ReservationDetailDto reservationDetailDto = new ReservationDetailDto( reservationId, reservationName, performanceName, venue, startedAt, reservedAt, seatNumbers, price, reservationStatus );

        return reservationDetailDto;
    }

    private String reservationPerformancePerformanceName(Reservation reservation) {
        Performance performance = reservation.getPerformance();
        if ( performance == null ) {
            return null;
        }
        return performance.getPerformanceName();
    }

    private String reservationPerformanceVenue(Reservation reservation) {
        Performance performance = reservation.getPerformance();
        if ( performance == null ) {
            return null;
        }
        return performance.getVenue();
    }

    private LocalDateTime reservationPerformanceStartedAt(Reservation reservation) {
        Performance performance = reservation.getPerformance();
        if ( performance == null ) {
            return null;
        }
        return performance.getStartedAt();
    }

    private String reservationMemberUsername(Reservation reservation) {
        Member member = reservation.getMember();
        if ( member == null ) {
            return null;
        }
        return member.getUsername();
    }
}

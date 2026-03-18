package com.grape.ticketing.domain;

import com.grape.ticketing.domain.status.SeatStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Seat extends BaseEntitiy{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    private String seatNumber;

    private SeatStatus seatStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @OneToMany(mappedBy = "seat")
    private List<ReservationSeat> reservationSeats = new ArrayList<>();
}

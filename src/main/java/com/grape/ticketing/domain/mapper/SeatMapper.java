package com.grape.ticketing.domain.mapper;

import com.grape.ticketing.domain.Seat;
import com.grape.ticketing.dto.SeatTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper( componentModel = "spring")
public interface SeatMapper {
    public SeatTO toSeatTO(Seat seat);
    public Seat toSeat(SeatTO seatTO);

    public List<SeatTO> toSeatTOList(List<Seat> list);
}

package com.grape.ticketing.domain.mapper;

import com.grape.ticketing.domain.Performance;
import com.grape.ticketing.dto.performance.PerformanceTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PerformanceMapper {

    @Mapping(source = "venue.venueName", target = "venue")
    PerformanceTO.PerformanceList toPerformanceListTO(Performance performance);

    @Mapping(source = "venue.venueName", target = "venue")
    @Mapping(source = "venue.address", target = "address")
    @Mapping(source = "venue.latitude", target = "latitude")
    @Mapping(source = "venue.longitude", target = "longitude")
    PerformanceTO.PerformanceRes toPerformanceTO(Performance performance);
}
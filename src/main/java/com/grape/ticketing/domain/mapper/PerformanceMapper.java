package com.grape.ticketing.domain.mapper;

import com.grape.ticketing.domain.Performance;
import com.grape.ticketing.dto.PerformanceTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PerformanceMapper {
    public PerformanceTO.PerformanceList toPerformanceTO(Performance performance);
}

package com.grape.ticketing.domain.mapper;

import com.grape.ticketing.domain.Performance;
import com.grape.ticketing.dto.PerformanceTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PerformanceMapper {
    public PerformanceTO.PerformanceList toPerformanceListTO(Performance performance);
    public PerformanceTO.PerformanceRes toPerformanceTO(Performance performance);
}

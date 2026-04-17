package com.grape.ticketing.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminDashboardResponseDto {
    private long totalMembers;
    private long totalPerformances;
    private long totalReservations;
    private long totalPayments;
}
package com.grape.ticketing.init;

import com.grape.ticketing.domain.Performance;
import com.grape.ticketing.domain.Seat;
import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.domain.status.SeatStatus;
import com.grape.ticketing.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

//@Component
@RequiredArgsConstructor
public class DummyDataInit implements CommandLineRunner {
    private final MemberRepository memberRepository;
    private final SeatRepository seatRepository;
    private final PerformanceRepository performanceRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        createMembers();
        createSeatsForExistingPerformances();
    }

    private void createMembers() {
        Member admin = new Member("kim",
                "test@gmail.com",
                "admin",
                passwordEncoder.encode("1234"),
                "ROLE_ADMIN");

        memberRepository.save(admin);

        for (int i = 1; i <= 100; i++) {
            Member user = new Member("name",
                    "user@gmail.com",
                    "user" + (i+1),
                    passwordEncoder.encode("1234"),
                    "ROLE_USER");

            memberRepository.save(user);
        }
    }

//    private void createPerformancesAndSeats() {
//        for (int i = 1; i <= 4; i++) {
//            Performance performance = new Performance();
//            performance.setPerformanceName("공연 " + i);
//            performance.setVenue("공연장 " + i);
//            performance.setPerformanceStatus(PerformanceStatus.ON_SALE);
//            performance.setStartedAt(LocalDateTime.now().plusDays(i));
//            performance.setPerformanceTime(120);
//            performance.setPrice(80000);
//
//            performanceRepository.save(performance);
//
//            createSeatsForPerformance(performance);
//        }
//    }

    private void createSeatsForExistingPerformances() {
        List<Performance> performances = performanceRepository.findAll();

        if (performances.isEmpty()) {
            throw new IllegalArgumentException("공연 데이터가 없습니다.");
        }

        for (Performance performance : performances) {
            createSeatsForPerformance(performance);
        }
    }

    private void createSeatsForPerformance(Performance performance) {
        List<Seat> seats = new ArrayList<>();

        for (char row = 'A'; row <= 'J'; row++) {
            for (int col = 1; col <= 1000; col++) {
                Seat seat = new Seat();
                seat.setSeatNumber(row + String.valueOf(col)); // A1 ~ J10
                seat.setSeatStatus(SeatStatus.AVAILABLE);
                seat.setPerformance(performance);
                seats.add(seat);
            }
        }

        seatRepository.saveAll(seats);
    }
}

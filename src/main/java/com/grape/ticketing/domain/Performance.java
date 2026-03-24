package com.grape.ticketing.domain;

import com.grape.ticketing.domain.status.PerformanceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Performance extends BaseEntitiy {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_id")
    private Long id;

    private String performanceName;
    private int price;
    private String venue;

    private int performanceTime; //공연 소요시간
    private LocalDateTime startedAt; //공연 시작시간
    private LocalDate startDate;  //공연 시작날짜
    private LocalDate endDate;    //공연 종료날짜

    @Enumerated(EnumType.STRING)
    private PerformanceStatus performanceStatus;
    private String imageUrl;

    @OneToMany(mappedBy = "performance")
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "performance")
    private List<Seat> seats = new ArrayList<>();
}

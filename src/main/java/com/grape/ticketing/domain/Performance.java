package com.grape.ticketing.domain;

import com.grape.ticketing.domain.status.PerformanceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Performance extends BaseEntitiy {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_id")
    private Long id;

    private String performanceName;
    private int price;
    private String venue;

    private int performanceTime; //공연 소요시간
    private LocalDateTime startedAt; //공연 시작시간 /끝나는시간은 뺐음

    @Enumerated(EnumType.STRING)
    private PerformanceStatus performanceStatus;

    @OneToMany(mappedBy = "performance")
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "performance")
    private List<Seat> seats = new ArrayList<>();
}

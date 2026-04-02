package com.grape.ticketing.domain;

import com.grape.ticketing.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "payment")
public class Payment extends BaseEntitiy{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;
    private Long amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservaiton_id")
    private Reservation reservation;

    private String orderId;

    private LocalDateTime paymentAt;
    private String paymentKey;

}

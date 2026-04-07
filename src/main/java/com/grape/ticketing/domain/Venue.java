package com.grape.ticketing.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter @Table(name = "venue")
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String venueName;
    private String address;

    private Double latitude;
    private Double longitude;

    @OneToMany(mappedBy = "venue")
    private List<Performance> performances = new ArrayList<>();


}

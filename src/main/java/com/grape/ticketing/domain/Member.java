package com.grape.ticketing.domain;

import com.grape.ticketing.domain.status.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "Member")
public class Member extends BaseEntitiy{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String username;
    private String password;

    private Role role;

    @OneToMany(mappedBy = "member")
    private List<Reservation> reservations = new ArrayList<>();

}

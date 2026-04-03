package com.grape.ticketing.repository;

import com.grape.ticketing.domain.member.Member;
import com.grape.ticketing.dto.member.MemberTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByUsername(String username);
    Optional<Member> findByEmail(String email);
    boolean existsByUsername(String username);
}

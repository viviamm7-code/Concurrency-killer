package com.grape.ticketing.repository;

import com.grape.ticketing.domain.member.SocialAccount;
import com.grape.ticketing.domain.member.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    Optional<SocialAccount> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
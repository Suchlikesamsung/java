package com.example.api.domain.member.repository;

import com.example.api.domain.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUserid(String userid);

    Optional<RefreshToken> findByToken(String token);

    void deleteByUserid(String userid);
}

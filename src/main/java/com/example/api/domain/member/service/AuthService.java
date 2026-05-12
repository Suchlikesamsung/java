package com.example.api.domain.member.service;

import com.example.api.domain.member.dto.LoginRequest;
import com.example.api.domain.member.dto.TokenResponse;
import com.example.api.domain.member.entity.Member;
import com.example.api.domain.member.entity.RefreshToken;
import com.example.api.domain.member.repository.MemberRepository;
import com.example.api.domain.member.repository.RefreshTokenRepository;
import com.example.api.global.error.BusinessException;
import com.example.api.global.error.ErrorCode;
import com.example.api.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private static final String GRANT_TYPE = "Bearer";

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByUserid(request.getUserid())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_LOGIN));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_LOGIN);
        }

        return issueTokens(member.getUserid());
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (stored.isExpired(LocalDateTime.now())) {
            refreshTokenRepository.delete(stored);
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        }

        return issueTokens(stored.getUserid());
    }

    public void logout(String userid) {
        refreshTokenRepository.deleteByUserid(userid);
    }

    private TokenResponse issueTokens(String userid) {
        String accessToken = jwtTokenProvider.createAccessToken(userid);
        String refreshToken = jwtTokenProvider.createRefreshToken(userid);
        LocalDateTime expiresAt = LocalDateTime.now()
                .plus(jwtTokenProvider.getRefreshExpirationMs(), ChronoUnit.MILLIS);

        refreshTokenRepository.findByUserid(userid)
                .ifPresentOrElse(
                        existing -> existing.rotate(refreshToken, expiresAt),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .userid(userid)
                                        .token(refreshToken)
                                        .expiresAt(expiresAt)
                                        .build()));

        return TokenResponse.builder()
                .grantType(GRANT_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}

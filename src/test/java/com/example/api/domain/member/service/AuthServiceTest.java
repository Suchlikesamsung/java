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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("로그인 성공 시 액세스/리프레시 토큰을 발급하고 저장한다")
    void login() {
        Member member = createMember("hong");
        LoginRequest request = loginRequest("hong", "password1234");
        when(memberRepository.findByUserid("hong")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("password1234", "password1234")).thenReturn(true);
        when(jwtTokenProvider.createAccessToken("hong")).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken("hong")).thenReturn("refresh-token");
        when(jwtTokenProvider.getRefreshExpirationMs()).thenReturn(1_209_600_000L);
        when(refreshTokenRepository.findByUserid("hong")).thenReturn(Optional.empty());

        TokenResponse response = authService.login(request);

        assertThat(response.getGrantType()).isEqualTo("Bearer");
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getUserid()).isEqualTo("hong");
        assertThat(captor.getValue().getToken()).isEqualTo("refresh-token");
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 INVALID_LOGIN 예외가 발생한다")
    void loginInvalidPassword() {
        Member member = createMember("hong");
        LoginRequest request = loginRequest("hong", "wrong-password");
        when(memberRepository.findByUserid("hong")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("wrong-password", "password1234")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_LOGIN);

        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("기존 리프레시 토큰이 있으면 회전한다")
    void loginRotatesExistingToken() {
        Member member = createMember("hong");
        RefreshToken existing = RefreshToken.builder()
                .userid("hong")
                .token("old-refresh")
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
        LoginRequest request = loginRequest("hong", "password1234");
        when(memberRepository.findByUserid("hong")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("password1234", "password1234")).thenReturn(true);
        when(jwtTokenProvider.createAccessToken("hong")).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken("hong")).thenReturn("new-refresh");
        when(jwtTokenProvider.getRefreshExpirationMs()).thenReturn(1_209_600_000L);
        when(refreshTokenRepository.findByUserid("hong")).thenReturn(Optional.of(existing));

        authService.login(request);

        assertThat(existing.getToken()).isEqualTo("new-refresh");
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("유효한 리프레시 토큰으로 재발급한다")
    void refresh() {
        RefreshToken stored = RefreshToken.builder()
                .userid("hong")
                .token("refresh-token")
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
        when(jwtTokenProvider.validateRefreshToken("refresh-token")).thenReturn(true);
        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.of(stored));
        when(jwtTokenProvider.createAccessToken("hong")).thenReturn("new-access");
        when(jwtTokenProvider.createRefreshToken("hong")).thenReturn("new-refresh");
        when(jwtTokenProvider.getRefreshExpirationMs()).thenReturn(1_209_600_000L);
        when(refreshTokenRepository.findByUserid("hong")).thenReturn(Optional.of(stored));

        TokenResponse response = authService.refresh("refresh-token");

        assertThat(response.getAccessToken()).isEqualTo("new-access");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh");
        assertThat(stored.getToken()).isEqualTo("new-refresh");
    }

    @Test
    @DisplayName("서명이 유효하지 않으면 INVALID_TOKEN 예외가 발생한다")
    void refreshInvalidSignature() {
        when(jwtTokenProvider.validateRefreshToken("bad-token")).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh("bad-token"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("DB 에 없는 리프레시 토큰이면 INVALID_TOKEN 예외가 발생한다")
    void refreshNotStored() {
        when(jwtTokenProvider.validateRefreshToken("refresh-token")).thenReturn(true);
        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh("refresh-token"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("저장된 리프레시 토큰이 만료되었으면 삭제하고 EXPIRED_TOKEN 예외가 발생한다")
    void refreshExpired() {
        RefreshToken expired = RefreshToken.builder()
                .userid("hong")
                .token("refresh-token")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .build();
        when(jwtTokenProvider.validateRefreshToken("refresh-token")).thenReturn(true);
        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> authService.refresh("refresh-token"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EXPIRED_TOKEN);

        verify(refreshTokenRepository).delete(expired);
    }

    @Test
    @DisplayName("로그아웃 시 해당 회원의 리프레시 토큰을 삭제한다")
    void logout() {
        authService.logout("hong");

        verify(refreshTokenRepository).deleteByUserid("hong");
    }

    private Member createMember(String userid) {
        return Member.builder()
                .userid(userid)
                .password("password1234")
                .username("홍길동")
                .email(userid + "@example.com")
                .build();
    }

    private LoginRequest loginRequest(String userid, String password) {
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "userid", userid);
        ReflectionTestUtils.setField(request, "password", password);
        return request;
    }
}

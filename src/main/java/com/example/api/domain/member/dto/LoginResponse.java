package com.example.api.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "로그인 응답")
public class LoginResponse {

    @Schema(description = "토큰 타입", example = "Bearer")
    private String grantType;

    @Schema(description = "JWT 액세스 토큰")
    private String accessToken;
}

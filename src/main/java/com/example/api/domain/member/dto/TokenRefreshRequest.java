package com.example.api.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "토큰 재발급 요청")
public class TokenRefreshRequest {

    @Schema(description = "리프레시 토큰")
    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;
}

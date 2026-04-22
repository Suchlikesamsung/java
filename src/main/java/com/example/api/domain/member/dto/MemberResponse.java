package com.example.api.domain.member.dto;

import com.example.api.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "회원 응답")
public class MemberResponse {

    @Schema(description = "회원 순번", example = "1")
    private Integer seq;

    @Schema(description = "사용자 아이디", example = "hong")
    private String userid;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String username;

    @Schema(description = "이메일", example = "hong@example.com")
    private String email;

    @Schema(description = "생성 일시", example = "2026-04-22T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시", example = "2026-04-22T10:30:00")
    private LocalDateTime updatedAt;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .seq(member.getSeq())
                .userid(member.getUserid())
                .username(member.getUsername())
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}

package com.example.api.domain.member.dto;

import com.example.api.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberResponse {

    private Integer seq;
    private String userid;
    private String username;
    private String email;
    private LocalDateTime createdAt;
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

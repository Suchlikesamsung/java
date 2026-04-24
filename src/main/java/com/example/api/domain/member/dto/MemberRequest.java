package com.example.api.domain.member.dto;

import com.example.api.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "회원 생성/수정 요청")
public class MemberRequest {

    @Schema(description = "사용자 아이디", example = "hong")
    @NotBlank(message = "아이디는 필수입니다.")
    private String userid;

    @Schema(description = "비밀번호", example = "password1234")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Schema(description = "사용자 이름", example = "홍길동")
    @NotBlank(message = "이름은 필수입니다.")
    private String username;

    @Schema(description = "이메일", example = "hong@example.com")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    public Member toEntity() {
        return toEntity(password);
    }

    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .userid(userid)
                .password(encodedPassword)
                .username(username)
                .email(email)
                .build();
    }
}

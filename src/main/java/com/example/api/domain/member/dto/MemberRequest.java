package com.example.api.domain.member.dto;

import com.example.api.domain.member.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberRequest {

    @NotBlank(message = "아이디는 필수입니다.")
    private String userid;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    private String username;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    public Member toEntity() {
        return Member.builder()
                .userid(userid)
                .password(password)
                .username(username)
                .email(email)
                .build();
    }
}

package com.example.api.domain.member.service;

import com.example.api.domain.member.dto.MemberRequest;
import com.example.api.domain.member.dto.MemberResponse;
import com.example.api.domain.member.entity.Member;
import com.example.api.domain.member.repository.MemberRepository;
import com.example.api.global.error.BusinessException;
import com.example.api.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("전체 회원 목록을 조회한다")
    void findAll() {
        Member member = createMember("hong", "홍길동", "hong@example.com");
        when(memberRepository.findAll()).thenReturn(List.of(member));

        List<MemberResponse> responses = memberService.findAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getUserid()).isEqualTo("hong");
        assertThat(responses.get(0).getUsername()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("사용자 아이디로 회원을 조회한다")
    void findByUserid() {
        Member member = createMember("hong", "홍길동", "hong@example.com");
        when(memberRepository.findByUserid("hong")).thenReturn(Optional.of(member));

        MemberResponse response = memberService.findByUserid("hong");

        assertThat(response.getUserid()).isEqualTo("hong");
        assertThat(response.getEmail()).isEqualTo("hong@example.com");
    }

    @Test
    @DisplayName("존재하지 않는 회원 조회 시 MEMBER_NOT_FOUND 예외가 발생한다")
    void findByUseridNotFound() {
        when(memberRepository.findByUserid("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.findByUserid("unknown"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("키워드로 회원을 검색한다")
    void search() {
        Member member = createMember("hong", "홍길동", "hong@example.com");
        when(memberRepository.findByUsernameContaining("홍")).thenReturn(List.of(member));

        List<MemberResponse> responses = memberService.search("홍");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getUsername()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("회원 생성 요청을 저장하고 응답으로 변환한다")
    void create() {
        MemberRequest request = createRequest("hong", "password1234", "홍길동", "hong@example.com");
        Member savedMember = createMember("hong", "홍길동", "hong@example.com");
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        MemberResponse response = memberService.create(request);

        assertThat(response.getUserid()).isEqualTo("hong");
        assertThat(response.getUsername()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("회원 정보를 수정한다")
    void update() {
        Member member = createMember("hong", "홍길동", "hong@example.com");
        MemberRequest request = createRequest("hong", "new-password", "홍길동2", "hong2@example.com");
        when(memberRepository.findByUserid("hong")).thenReturn(Optional.of(member));

        MemberResponse response = memberService.update("hong", request);

        assertThat(response.getUsername()).isEqualTo("홍길동2");
        assertThat(response.getEmail()).isEqualTo("hong2@example.com");
    }

    @Test
    @DisplayName("회원 삭제 시 Repository delete를 호출한다")
    void delete() {
        Member member = createMember("hong", "홍길동", "hong@example.com");
        when(memberRepository.findByUserid("hong")).thenReturn(Optional.of(member));

        memberService.delete("hong");

        verify(memberRepository).delete(member);
    }

    private Member createMember(String userid, String username, String email) {
        return Member.builder()
                .userid(userid)
                .password("password1234")
                .username(username)
                .email(email)
                .build();
    }

    private MemberRequest createRequest(String userid, String password, String username, String email) {
        MemberRequest request = new MemberRequest();
        ReflectionTestUtils.setField(request, "userid", userid);
        ReflectionTestUtils.setField(request, "password", password);
        ReflectionTestUtils.setField(request, "username", username);
        ReflectionTestUtils.setField(request, "email", email);
        return request;
    }
}

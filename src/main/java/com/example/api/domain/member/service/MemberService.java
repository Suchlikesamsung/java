package com.example.api.domain.member.service;

import com.example.api.domain.member.dto.MemberRequest;
import com.example.api.domain.member.dto.MemberResponse;
import com.example.api.domain.member.entity.Member;
import com.example.api.domain.member.repository.MemberRepository;
import com.example.api.global.common.PageResponse;
import com.example.api.global.error.BusinessException;
import com.example.api.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public PageResponse<MemberResponse> findAll(Pageable pageable) {
        return PageResponse.from(memberRepository.findAll(pageable).map(MemberResponse::from));
    }

    public MemberResponse findByUserid(String userid) {
        Member member = memberRepository.findByUserid(userid)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberResponse.from(member);
    }

    public PageResponse<MemberResponse> search(String keyword, Pageable pageable) {
        return PageResponse.from(
                memberRepository.findByUsernameContaining(keyword, pageable).map(MemberResponse::from));
    }

    @Transactional
    public MemberResponse create(MemberRequest request) {
        Member member = request.toEntity(passwordEncoder.encode(request.getPassword()));
        Member saved = memberRepository.save(member);
        return MemberResponse.from(saved);
    }

    @Transactional
    public MemberResponse update(String userid, MemberRequest request) {
        Member member = memberRepository.findByUserid(userid)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        member.update(passwordEncoder.encode(request.getPassword()), request.getUsername(), request.getEmail());
        return MemberResponse.from(member);
    }

    @Transactional
    public void delete(String userid) {
        Member member = memberRepository.findByUserid(userid)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        memberRepository.delete(member);
    }
}

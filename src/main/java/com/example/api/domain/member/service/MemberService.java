package com.example.api.domain.member.service;

import com.example.api.domain.member.dto.MemberRequest;
import com.example.api.domain.member.dto.MemberResponse;
import com.example.api.domain.member.entity.Member;
import com.example.api.domain.member.repository.MemberRepository;
import com.example.api.global.error.BusinessException;
import com.example.api.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public List<MemberResponse> findAll() {
        return memberRepository.findAll().stream()
                .map(MemberResponse::from)
                .collect(Collectors.toList());
    }

    public MemberResponse findByUserid(String userid) {
        Member member = memberRepository.findByUserid(userid)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberResponse.from(member);
    }

    public List<MemberResponse> search(String keyword) {
        return memberRepository.findByUsernameContaining(keyword).stream()
                .map(MemberResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public MemberResponse create(MemberRequest request) {
        Member member = request.toEntity();
        Member saved = memberRepository.save(member);
        return MemberResponse.from(saved);
    }

    @Transactional
    public MemberResponse update(String userid, MemberRequest request) {
        Member member = memberRepository.findByUserid(userid)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        member.update(request.getPassword(), request.getUsername(), request.getEmail());
        return MemberResponse.from(member);
    }

    @Transactional
    public void delete(String userid) {
        Member member = memberRepository.findByUserid(userid)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        memberRepository.delete(member);
    }
}

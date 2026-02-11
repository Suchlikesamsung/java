package com.example.api.domain.member.repository;

import com.example.api.domain.member.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    List<Member> findByUsernameContaining(String keyword);
}

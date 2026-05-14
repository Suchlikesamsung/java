package com.example.api.domain.member.repository;

import com.example.api.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

    Page<Member> findByUsernameContaining(String keyword, Pageable pageable);
}

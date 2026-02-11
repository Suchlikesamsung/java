package com.example.api.domain.member.repository;

import com.example.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer>, MemberRepositoryCustom {

    Optional<Member> findByUserid(String userid);
}

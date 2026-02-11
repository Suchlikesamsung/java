package com.example.api.domain.member.repository;

import com.example.api.domain.member.entity.Member;
import com.example.api.domain.member.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findByUsernameContaining(String keyword) {
        QMember member = QMember.member;

        return queryFactory
                .selectFrom(member)
                .where(member.username.containsIgnoreCase(keyword))
                .orderBy(member.seq.asc())
                .fetch();
    }
}

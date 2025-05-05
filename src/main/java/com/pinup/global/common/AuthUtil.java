package com.pinup.global.common;

import com.pinup.domain.member.entity.Member;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.response.ErrorCode;
import com.pinup.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    private final MemberRepository memberRepository;

    @Autowired
    public AuthUtil(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member getLoginMember() {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Member getValidMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }
}

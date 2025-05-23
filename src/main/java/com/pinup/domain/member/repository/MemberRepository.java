package com.pinup.domain.member.repository;

import com.pinup.domain.member.entity.LoginType;
import com.pinup.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByProviderId(String providerId);
    Optional<Member> findByLoginTypeAndProviderId(LoginType loginType, String providerId);
    boolean existsByNickname(String nickname);
    boolean existsByNicknameAndIdNot(String nickname, Long id);
    List<Member> findAllByNicknameContaining(String nickname);
}
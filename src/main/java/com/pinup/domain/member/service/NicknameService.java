package com.pinup.domain.member.service;

import com.pinup.domain.member.entity.Member;
import com.pinup.domain.member.exception.NicknameUpdateTimeLimitException;
import com.pinup.domain.member.repository.MemberRepository;
import com.pinup.global.exception.EntityAlreadyExistException;
import com.pinup.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NicknameService {

    private final MemberRepository memberRepository;

    public void validateNicknameUpdate(Member member, String newNickname) {
        validateNicknameUpdateTimeLimit(member);
        if (memberRepository.existsByNicknameAndIdNot(newNickname, member.getId())) {
            throw new EntityAlreadyExistException(ErrorCode.ALREADY_EXIST_NICKNAME);
        }
    }

    public boolean isNicknameEqual(String prevNickname, String newNickname) {
        return prevNickname.equals(newNickname);
    }

    private void validateNicknameUpdateTimeLimit(Member member) {
        if (!member.canUpdateNickname()) {
            throw new NicknameUpdateTimeLimitException();
        }
    }
}

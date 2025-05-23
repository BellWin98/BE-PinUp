package com.pinup.domain.friend.service;

import com.pinup.domain.friend.entity.FriendShip;
import com.pinup.domain.friend.repository.FriendShipRepository;
import com.pinup.domain.member.dto.response.MemberResponse;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.member.repository.MemberRepository;
import com.pinup.global.common.AuthUtil;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FriendShipService {

    private final FriendShipRepository friendShipRepository;
    private final MemberRepository memberRepository;
    private final AuthUtil authUtil;

    @Transactional(readOnly = true)
    public Page<MemberResponse> getAllFriendsOfMember(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        List<MemberResponse> memberResponses = friendShipRepository.findAllByMember(member).stream()
                .map(friendShip -> MemberResponse.from(friendShip.getFriend()))
                .sorted(Comparator.comparing(MemberResponse::getNickname))
                .toList();

        return new PageImpl<>(memberResponses, pageable, memberResponses.size());
    }

    @Transactional(readOnly = true)
    public Page<MemberResponse> getAllFriendsOfMe(Pageable pageable) {
        Member loginMember = authUtil.getLoginMember();

        List<MemberResponse> memberResponses = friendShipRepository.findAllByMember(loginMember).stream()
                .map(friendShip -> MemberResponse.from(friendShip.getFriend()))
                .sorted(Comparator.comparing(MemberResponse::getNickname))
                .toList();

        return new PageImpl<>(memberResponses, pageable, memberResponses.size());
    }

    @Transactional
    public void removeFriend(Long friendId) {
        Member currentUser = authUtil.getLoginMember();
        Member friend = memberRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FRIEND_NOT_FOUND));
        FriendShip friendship1 = friendShipRepository.findByMemberAndFriend(currentUser, friend)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FRIEND_NOT_FOUND));
        FriendShip friendship2 = friendShipRepository.findByMemberAndFriend(friend, currentUser)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FRIEND_NOT_FOUND));
        friendShipRepository.delete(friendship1);
        friendShipRepository.delete(friendship2);
    }

    @Transactional(readOnly = true)
    public MemberResponse searchMyFriendInfoByNickname(String nickname) {
        Member member = authUtil.getLoginMember();

        return friendShipRepository.findAllByMember(member)
                .stream()
                .map(FriendShip::getFriend)
                .filter(friend -> friend.getNickname().equals(nickname.trim()))
                .map(MemberResponse::from)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FRIEND_NOT_FOUND));
    }

    @Transactional
    public void createFriendShip(Member member, Member friend) {
        FriendShip friendShip1 = FriendShip.builder()
                .member(member)
                .friend(friend)
                .build();
        friendShipRepository.save(friendShip1);

        FriendShip friendShip2 = FriendShip.builder()
                .member(friend)
                .friend(member)
                .build();
        friendShipRepository.save(friendShip2);
    }
}
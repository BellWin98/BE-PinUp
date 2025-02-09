package com.pinup.domain.friend.service;

import com.pinup.domain.member.dto.response.MemberResponse;
import com.pinup.domain.friend.entity.FriendShip;
import com.pinup.domain.member.entity.Member;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.response.ErrorCode;
import com.pinup.global.common.AuthUtil;
import com.pinup.domain.friend.repository.FriendShipRepository;
import com.pinup.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FriendShipService {

    private final FriendShipRepository friendShipRepository;
    private final MemberRepository memberRepository;
    private final AuthUtil authUtil;

    @Transactional(readOnly = true)
    public List<MemberResponse> getUserAllFriends(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        return friendShipRepository.findAllByMember(member)
                .stream()
                .map(friendShip -> MemberResponse.from(friendShip.getFriend()))
                .collect(Collectors.toList());
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

    @Transactional
    public void removeFriend(Long friendId) {

        Member currentUser = authUtil.getLoginMember();
        Member friend = memberRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FRIEND_NOT_FOUND));

        FriendShip friendship1 = friendShipRepository.findByMemberAndFriend(currentUser, friend)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FRIEND_NOT_FOUND));
        friendShipRepository.delete(friendship1);

        FriendShip friendship2 = friendShipRepository.findByMemberAndFriend(friend, currentUser)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FRIEND_NOT_FOUND));
        friendShipRepository.delete(friendship2);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getMyAllFriends() {

        Member currentUser = authUtil.getLoginMember();

        List<MemberResponse> friendList = friendShipRepository.findAllByMember(currentUser)
                .stream()
                .map(friendShip -> MemberResponse.from(friendShip.getFriend()))
                .collect(Collectors.toList());
        friendList.sort(Comparator.comparing(MemberResponse::getNickname));

        return friendList;
    }

    @Transactional(readOnly = true)
    public MemberResponse searchMyFriendInfoByNickname(String query) {
        Member member = authUtil.getLoginMember();

        return friendShipRepository.findAllByMember(member)
                .stream()
                .map(FriendShip::getFriend)
                .filter(friend -> friend.getNickname().equals(query.trim()))
                .map(MemberResponse::from)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.FRIEND_NOT_FOUND));
    }

    public boolean existsFriendship(Member sender, Member receiver) {
        return friendShipRepository.findByMemberAndFriend(sender, receiver)
                .isPresent();
    }
}
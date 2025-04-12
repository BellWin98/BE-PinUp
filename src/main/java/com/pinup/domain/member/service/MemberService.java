package com.pinup.domain.member.service;

import com.pinup.domain.friend.entity.FriendRequest;
import com.pinup.domain.friend.entity.FriendRequestStatus;
import com.pinup.domain.friend.repository.FriendRequestRepository;
import com.pinup.domain.friend.repository.FriendShipRepository;
import com.pinup.domain.member.dto.request.UpdateMemberInfoAfterLoginRequest;
import com.pinup.domain.member.dto.request.UpdateProfileRequest;
import com.pinup.domain.member.dto.response.MemberInfoResponse;
import com.pinup.domain.member.dto.response.MemberResponse;
import com.pinup.domain.member.dto.response.SearchMemberResponse;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.member.entity.MemberRelationType;
import com.pinup.domain.member.repository.MemberRepository;
import com.pinup.global.common.AuthUtil;
import com.pinup.global.common.image.repository.ImageRepository;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final AuthUtil authUtil;

    private final NicknameService nicknameService;
    private final ProfileImageService profileImageService;

    private final MemberRepository memberRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendShipRepository friendShipRepository;

    @Transactional(readOnly = true)
    public List<SearchMemberResponse> searchMembers(String nickname) {
        Member loginMember = authUtil.getLoginMember();
        if (nickname.isBlank()) {
            return null;
        }
        List<Member> members = memberRepository.findAllByNicknameContaining(nickname);

        return members.stream()
                .map(member -> SearchMemberResponse.builder()
                        .memberResponse(MemberResponse.from(member))
                        .relationType(determineRelationType(loginMember, member))
                        .build())
//                .filter(searchMemberResponse -> searchMemberResponse.getRelationType() != MemberRelationType.SELF)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MemberInfoResponse getMemberInfo(Long memberId) {
        Member loginMember = authUtil.getLoginMember();
        Member targetMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        Long friendRequestId = friendRequestRepository
                .findBySenderAndReceiverAndFriendRequestStatus(loginMember, targetMember, FriendRequestStatus.PENDING)
                .map(FriendRequest::getId).orElse(null);

        return MemberInfoResponse.builder()
                .memberResponse(MemberResponse.from(targetMember))
                .relationType(determineRelationType(loginMember, targetMember))
                .friendRequestId(friendRequestId)
                .build();
    }

    @Transactional(readOnly = true)
    public MemberInfoResponse getMyInfo() {
        Member loginMember = authUtil.getLoginMember();

        return MemberInfoResponse.builder()
                .memberResponse(MemberResponse.from(loginMember))
                .relationType(MemberRelationType.SELF)
                .friendRequestId(null)
                .build();
    }

    @Transactional
    public void updateProfile(UpdateProfileRequest updateProfileRequest) {
        Member loginMember = authUtil.getLoginMember();
        String newNickname = updateProfileRequest.getNickname();
        if (!nicknameService.isNicknameEqual(loginMember.getNickname(), newNickname)) {
            nicknameService.validateNicknameUpdate(loginMember, newNickname);
            loginMember.updateNickname(newNickname);
        }
        loginMember.updateBio(updateProfileRequest.getBio());
        profileImageService.updateProfileImage(loginMember, updateProfileRequest.getProfileImageUrl());
    }

    @Transactional(readOnly = true)
    public boolean checkNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Transactional
    public MemberResponse updateInfoAfterLogin(UpdateMemberInfoAfterLoginRequest request) {
        Member loginMember = authUtil.getLoginMember();
        loginMember.updateNickname(request.getNickname());
        loginMember.updateTermsOfMarketing(request.getTermsOfMarketing());
        profileImageService.updateProfileImage(loginMember, request.getProfileImageUrl());

        return MemberResponse.from(memberRepository.save(loginMember));
    }

    private MemberRelationType determineRelationType(Member loginMember, Member member) {
        if (loginMember.equals(member)) {
            return MemberRelationType.SELF;
        }
        if (friendShipRepository.existsByMemberAndFriend(loginMember, member)) {
            return MemberRelationType.FRIEND;
        }
        if (friendRequestRepository.existsBySenderAndReceiverAndFriendRequestStatus(
                loginMember, member, FriendRequestStatus.PENDING)) {
            return MemberRelationType.PENDING;
        }
        return MemberRelationType.STRANGER;
    }
}
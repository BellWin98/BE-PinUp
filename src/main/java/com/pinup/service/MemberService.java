package com.pinup.service;

import com.pinup.cache.MemberCacheManager;
import com.pinup.dto.request.MemberInfoUpdateRequest;
import com.pinup.dto.request.UpdateMemberInfoAfterLoginRequest;
import com.pinup.dto.response.MemberResponse;
import com.pinup.dto.response.ProfileResponse;
import com.pinup.entity.Member;
import com.pinup.entity.Review;
import com.pinup.entity.ReviewImage;
import com.pinup.enums.FriendRequestStatus;
import com.pinup.enums.MemberRelationType;
import com.pinup.exception.NicknameUpdateTimeLimitException;
import com.pinup.global.exception.EntityAlreadyExistException;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.exception.ErrorCode;
import com.pinup.global.s3.S3Service;
import com.pinup.global.util.AuthUtil;
import com.pinup.repository.MemberRepository;
import com.pinup.repository.FriendRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {

    private static final String PROFILE_IMAGE_DIRECTORY = "profiles";

    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final MemberCacheManager memberCacheManager;
    private final FriendShipService friendShipService;
    private final FriendRequestRepository friendRequestRepository;
    private final AuthUtil authUtil;

    @Transactional(readOnly = true)
    @Cacheable(value = "member", key = "#nickname")
    public MemberResponse searchMembers(String nickname) {
        Member member = memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse getCurrentMemberInfo() {
        Member currentMember = authUtil.getLoginMember();
        return MemberResponse.from(currentMember);
    }

    @Transactional
    public void deleteMember() {
        Member currentMember = authUtil.getLoginMember();
        memberCacheManager.evictAllCaches(currentMember.getId());
        memberRepository.delete(currentMember);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberInfo(Long memberId) {
        return memberCacheManager.getMemberWithCache(memberId, () -> {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
            return MemberResponse.from(member);
        });
    }

    @Transactional(readOnly = true)
    public boolean checkNicknameDuplicate(String nickname) {
        return memberRepository.findByNickname(nickname).isPresent();
    }

    @Transactional
    public MemberResponse updateMemberInfo(MemberInfoUpdateRequest request, MultipartFile image) {
        Member member = authUtil.getLoginMember();
        validateNicknameUpdate(member, request.getNickname());
        member.updateNickname(request.getNickname());
        member.updateBio(request.getBio());
        if (member.getProfileImageUrl() != null) {
            s3Service.deleteFile(member.getProfileImageUrl());
        }
        String imageUrl = s3Service.uploadFile(PROFILE_IMAGE_DIRECTORY, image);
        member.updateProfileImage(imageUrl);
        memberRepository.save(member);
        memberCacheManager.evictAllCaches(member.getId());

        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse updateInfoAfterLogin(UpdateMemberInfoAfterLoginRequest request, MultipartFile multipartFile) {
        Member loginMember = authUtil.getLoginMember();
        String nickname = request.getNickname();
        String termsOfMarketing = request.getTermsOfMarketing();

        loginMember.updateNickname(nickname);
        loginMember.updateTermsOfMarketing(termsOfMarketing);

        String imageUrl = s3Service.uploadFile(PROFILE_IMAGE_DIRECTORY, multipartFile);
        loginMember.updateProfileImage(imageUrl);

        return MemberResponse.from(memberRepository.save(loginMember));
    }

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile() {
        Member currentMember = authUtil.getLoginMember();
        return getProfile(currentMember.getId());
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long memberId) {
        return memberCacheManager.getProfileWithCache(memberId, () -> {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
            return getProfileForMember(member);
        });
    }

    private void validateNicknameUpdate(Member member, String newNickname) {
        validateNicknameUpdateTimeLimit(member);
        validateNicknameDuplicate(newNickname);
    }

    private void validateNicknameUpdateTimeLimit(Member member) {
        if (!member.canUpdateNickname()) {
            throw new NicknameUpdateTimeLimitException();
        }
    }

    private void validateNicknameDuplicate(String nickname) {
        if (checkNicknameDuplicate(nickname)) {
            throw new EntityAlreadyExistException(ErrorCode.ALREADY_EXIST_NICKNAME);
        }
    }

    private ProfileResponse getProfileForMember(Member member) {
        Member currentMember = authUtil.getLoginMember();
        MemberRelationType relationType;

        if (currentMember.getId().equals(member.getId())) {
            relationType = MemberRelationType.SELF;
        } else if (friendShipService.existsFriendship(currentMember, member)) {
            relationType = MemberRelationType.FRIEND;
        } else if (friendRequestRepository.findBySenderAndReceiverAndFriendRequestStatus(
                currentMember, member, FriendRequestStatus.PENDING).isPresent()) {
            relationType = MemberRelationType.PENDING;
        } else {
            relationType = MemberRelationType.STRANGER;
        }

        double averageRating = member.getReviews().stream()
                .mapToDouble(Review::getStarRating)
                .average()
                .orElse(0.0);

        return ProfileResponse.builder()
                .member(MemberResponse.from(member))
                .reviewCount(member.getReviews().size())
                .friendCount(member.getFriendships().size())
                .averageRating(Math.round(averageRating * 10.0) / 10.0)
                .relationType(relationType)
                .build();
    }
}
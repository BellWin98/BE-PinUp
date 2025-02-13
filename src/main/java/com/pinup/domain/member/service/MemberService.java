package com.pinup.domain.member.service;

import com.pinup.global.config.redis.MemberCacheManager;
import com.pinup.domain.member.dto.request.MemberInfoUpdateRequest;
import com.pinup.domain.member.dto.request.UpdateMemberInfoAfterLoginRequest;
import com.pinup.domain.member.dto.response.FeedResponse;
import com.pinup.domain.member.dto.response.MemberResponse;
import com.pinup.domain.member.dto.response.ProfileResponse;
import com.pinup.domain.member.dto.response.MemberReviewResponse;
import com.pinup.domain.friend.entity.FriendShip;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.review.entity.Review;
import com.pinup.domain.friend.entity.FriendRequestStatus;
import com.pinup.domain.member.entity.MemberRelationType;
import com.pinup.domain.member.exception.NicknameUpdateTimeLimitException;
import com.pinup.global.exception.EntityAlreadyExistException;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.response.ErrorCode;
import com.pinup.global.config.s3.S3Service;
import com.pinup.global.common.AuthUtil;
import com.pinup.domain.friend.repository.FriendRequestRepository;
import com.pinup.domain.member.repository.MemberRepository;
import com.pinup.domain.friend.service.FriendShipService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

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
    private final ApplicationContext applicationContext;

    private MemberService getSpringProxy() {
        return applicationContext.getBean(MemberService.class);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> searchMembers(String nickname) {
        List<Member> members = memberRepository.findByNicknameContaining(nickname);
        return members.stream()
                .map(MemberResponse::from)
                .toList();
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

    @Transactional(readOnly = true)
    public FeedResponse getMyFeed() {
        Member loginMember = authUtil.getLoginMember();

        return this.getSpringProxy().buildFeedResponse(loginMember);
    }

    @Transactional(readOnly = true)
    public FeedResponse getMemberFeed(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        return this.getSpringProxy().buildFeedResponse(member);
    }

    public FeedResponse buildFeedResponse(Member member) {
        List<Review> reviews = member.getReviews();
        List<FriendShip> friends = member.getFriendships();

        double averageStarRating = reviews.stream()
                .mapToDouble(Review::getStarRating)
                .average()
                .orElse(0.0);

        double roundedAverageRating = BigDecimal.valueOf(averageStarRating)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();

        return FeedResponse.builder()
                .memberResponse(MemberResponse.from(member))
                .reviewCount(reviews.size())
                .averageStarRating(roundedAverageRating)
                .pinBuddyCount(friends.size())
                .memberReviews(reviews.stream()
                        .map(MemberReviewResponse::of)
                        .toList())
                .build();
    }
}
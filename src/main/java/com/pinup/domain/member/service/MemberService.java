package com.pinup.domain.member.service;

import com.pinup.domain.friend.entity.FriendRequestStatus;
import com.pinup.domain.friend.entity.FriendShip;
import com.pinup.domain.friend.repository.FriendRequestRepository;
import com.pinup.domain.friend.repository.FriendShipRepository;
import com.pinup.domain.member.dto.request.UpdateMemberInfoAfterLoginRequest;
import com.pinup.domain.member.dto.response.FeedResponse;
import com.pinup.domain.member.dto.response.MemberResponse;
import com.pinup.domain.member.dto.response.MemberReviewResponse;
import com.pinup.domain.member.dto.response.SearchMemberResponse;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.member.entity.MemberRelationType;
import com.pinup.domain.member.exception.NicknameUpdateTimeLimitException;
import com.pinup.domain.member.repository.MemberRepository;
import com.pinup.domain.review.entity.Review;
import com.pinup.global.common.AuthUtil;
import com.pinup.global.common.Formatter;
import com.pinup.global.config.s3.S3Service;
import com.pinup.global.exception.EntityAlreadyExistException;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MemberService {

    private static final String PROFILE_IMAGE_DIRECTORY = "profiles";

    private final MemberRepository memberRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendShipRepository friendShipRepository;
    private final AuthUtil authUtil;
    private final S3Service s3Service;

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
                        .reviewCount(member.getReviews().size())
                        .pinBuddyCount(member.getFriendships().size())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FeedResponse getMyFeed() {
        Member loginMember = authUtil.getLoginMember();

        return buildFeedResponse(loginMember, loginMember);
    }

    @Transactional(readOnly = true)
    public FeedResponse getMemberFeed(Long memberId) {
        Member loginMember = authUtil.getLoginMember();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        return buildFeedResponse(loginMember, member);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public boolean checkNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(nickname);
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

    private FeedResponse buildFeedResponse(Member loginMember, Member member) {
        List<Review> reviews = member.getReviews();
        List<FriendShip> friends = member.getFriendships();
        double averageStarRating = reviews.stream()
                .mapToDouble(Review::getStarRating)
                .average()
                .orElse(0.0);
        double roundedAverageRating = Formatter.formatStarRating(averageStarRating);
        return FeedResponse.builder()
                .memberResponse(MemberResponse.from(member))
                .reviewCount(reviews.size())
                .averageStarRating(roundedAverageRating)
                .pinBuddyCount(friends.size())
                .relationType(determineRelationType(loginMember, member))
                .memberReviews(reviews.stream()
                        .map(MemberReviewResponse::of)
                        .toList())
                .build();
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
}
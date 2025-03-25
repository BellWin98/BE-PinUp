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
import com.pinup.domain.member.exception.NicknameUpdateTimeLimitException;
import com.pinup.domain.member.repository.MemberRepository;
import com.pinup.domain.review.dto.response.PhotoReviewResponse;
import com.pinup.domain.review.dto.response.TextReviewResponse;
import com.pinup.domain.review.entity.Review;
import com.pinup.domain.review.entity.ReviewType;
import com.pinup.domain.review.repository.ReviewRepository;
import com.pinup.global.common.AuthUtil;
import com.pinup.global.config.s3.S3Service;
import com.pinup.global.exception.EntityAlreadyExistException;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ReviewRepository reviewRepository;

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
    public void updateProfile(UpdateProfileRequest updateProfileRequest, MultipartFile multipartFile) {
        Member loginMember = authUtil.getLoginMember();
        String newNickname = updateProfileRequest.getNickname();
        validateNicknameUpdate(loginMember, newNickname);
        loginMember.updateNickname(newNickname);
        loginMember.updateBio(updateProfileRequest.getBio());
        String newProfileImageUrl = null;
        if (multipartFile != null && !multipartFile.isEmpty()) {
            s3Service.deleteFile(loginMember.getProfileImageUrl());
            newProfileImageUrl = s3Service.uploadFile(PROFILE_IMAGE_DIRECTORY, multipartFile);
        }
        if (newProfileImageUrl != null) {
            loginMember.updateProfileImage(newProfileImageUrl);
        }
        memberRepository.save(loginMember);
    }

    @Transactional(readOnly = true)
    public Page<TextReviewResponse> getMemberTextReviews(Pageable pageable, Long memberId) {
        Member member = authUtil.getValidMember(memberId);
        Page<Review> reviewPage = reviewRepository.findAllByMemberAndTypeOrderByCreatedAtDesc(pageable, member, ReviewType.TEXT);

        return reviewPage.map(TextReviewResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<TextReviewResponse> getMyTextReviews(Pageable pageable) {
        Member loginMember = authUtil.getLoginMember();
        Page<Review> reviewPage = reviewRepository.findAllByMemberAndTypeOrderByCreatedAtDesc(pageable, loginMember, ReviewType.TEXT);

        return reviewPage.map(TextReviewResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<PhotoReviewResponse> getPhotoReviews(Pageable pageable, Long memberId) {
        Member member = authUtil.getValidMember(memberId);
        Page<Review> reviewPage = reviewRepository.findAllByMemberAndTypeOrderByCreatedAtDesc(pageable, member, ReviewType.PHOTO);

        return reviewPage.map(PhotoReviewResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<PhotoReviewResponse> getMyPhotoReviews(Pageable pageable) {
        Member loginMember = authUtil.getLoginMember();
        Page<Review> reviewPage = reviewRepository.findAllByMemberAndTypeOrderByCreatedAtDesc(pageable, loginMember, ReviewType.PHOTO);

        return reviewPage.map(PhotoReviewResponse::from);
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
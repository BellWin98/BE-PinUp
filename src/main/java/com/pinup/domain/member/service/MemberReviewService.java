package com.pinup.domain.member.service;

import com.pinup.domain.member.entity.Member;
import com.pinup.domain.review.dto.response.PhotoReviewResponse;
import com.pinup.domain.review.dto.response.TextReviewResponse;
import com.pinup.domain.review.entity.Review;
import com.pinup.domain.review.entity.ReviewType;
import com.pinup.domain.review.repository.ReviewRepository;
import com.pinup.global.common.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberReviewService {

    private final ReviewRepository reviewRepository;
    private final AuthUtil authUtil;

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
}

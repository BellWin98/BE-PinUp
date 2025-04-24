package com.pinup.domain.review.service;

import com.pinup.domain.friend.repository.FriendShipRepository;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.place.dto.request.PlaceRequest;
import com.pinup.domain.place.entity.Place;
import com.pinup.domain.place.repository.PlaceRepository;
import com.pinup.domain.review.dto.request.ReviewRequest;
import com.pinup.domain.review.dto.request.UpdateReviewRequest;
import com.pinup.domain.review.dto.response.ReviewResponse;
import com.pinup.domain.review.entity.Review;
import com.pinup.domain.review.entity.ReviewImage;
import com.pinup.domain.review.entity.ReviewType;
import com.pinup.domain.review.repository.ReviewRepository;
import com.pinup.global.common.AuthUtil;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.exception.UnauthorizedAccessException;
import com.pinup.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;
    private final FriendShipRepository friendShipRepository;
    private final AuthUtil authUtil;
    private final ReviewImageService reviewImageService;

    @Transactional
    public String register(ReviewRequest reviewRequest, PlaceRequest placeRequest) {
        Member loginMember = authUtil.getLoginMember();
        Place place = findOrCreatePlace(placeRequest);
        List<String> reviewImageUrls = reviewRequest.getReviewImageUrls();
        Review newReview = reviewRequest.toEntity();
        newReview.attachPlace(place);
        newReview.attachMember(loginMember);
        if (reviewImageUrls != null && !reviewImageUrls.isEmpty()) {
            reviewImageService.saveReviewImages(newReview, reviewImageUrls);
            newReview.setType(ReviewType.PHOTO);
        } else {
            newReview.setType(ReviewType.TEXT);
        }
        reviewRepository.save(newReview);

        return place.getKakaoPlaceId();
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long reviewId) {
        Member currentUser = authUtil.getLoginMember();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));
        validateReviewAccess(currentUser, review);
        return ReviewResponse.from(review);
    }

    @Transactional
    public void update(Long reviewId, UpdateReviewRequest updateReviewRequest) {
        Member loginMember = authUtil.getLoginMember();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));
        if (!loginMember.equals(review.getMember())) {
            throw new UnauthorizedAccessException(ErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
        }
        review.update(updateReviewRequest.getContent(), updateReviewRequest.getStarRating());
        reviewImageService.updateReviewImages(review, updateReviewRequest.getReviewImageUrls());
        List<ReviewImage> updatedReviewImages = review.getReviewImages();
        review.setType(updatedReviewImages != null && !updatedReviewImages.isEmpty() ? ReviewType.PHOTO : ReviewType.TEXT);
    }

    @Transactional
    public void delete(Long reviewId) {
        Member loginMember = authUtil.getLoginMember();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));
        if (!loginMember.equals(review.getMember())) {
            throw new UnauthorizedAccessException(ErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
        }
        if (!review.getReviewImages().isEmpty()) {
            reviewImageService.deleteReviewImages(review);
        }
        reviewRepository.delete(review);
    }

    /**
     * 카카오맵 ID로 DB에 등록된 업체 정보 조회
     * DB에 업체 미등록 시, 업체 신규 생성
     */
    private Place findOrCreatePlace(PlaceRequest placeRequest) {
        String kakaoPlaceId = placeRequest.getKakaoPlaceId();

        return placeRepository.findByKakaoPlaceId(kakaoPlaceId)
                .orElseGet(() -> placeRepository.save(placeRequest.toEntity()));
    }

    private void validateReviewAccess(Member currentUser, Review review) {
        if (!review.getMember().equals(currentUser) &&
                !friendShipRepository.existsByMemberAndFriend(currentUser, review.getMember())) {
            throw new EntityNotFoundException(ErrorCode.FRIEND_NOT_FOUND);
        }
    }
}

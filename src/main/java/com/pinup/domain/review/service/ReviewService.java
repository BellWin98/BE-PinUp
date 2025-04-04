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
import com.pinup.domain.review.repository.ReviewImageRepository;
import com.pinup.domain.review.repository.ReviewRepository;
import com.pinup.global.common.AuthUtil;
import com.pinup.global.common.ImageValidator;
import com.pinup.global.config.s3.S3Service;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.exception.FileProcessingException;
import com.pinup.global.exception.UnauthorizedAccessException;
import com.pinup.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {

    private static final String FILE_TYPE = "reviews";
    private static final int IMAGES_LIMIT = 3;

    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final FriendShipRepository friendShipRepository;
    private final AuthUtil authUtil;
    private final S3Service s3Service;
    private final ImageValidator imageValidator;

    @Transactional
    public String register(ReviewRequest reviewRequest, PlaceRequest placeRequest, List<MultipartFile> images) {
        Member loginMember = authUtil.getLoginMember();
        Place place = findOrCreatePlace(placeRequest);
        Review newReview = reviewRequest.toEntity();
        newReview.attachPlace(place);
        newReview.attachMember(loginMember);
        newReview.setType(images != null && !images.isEmpty() ? ReviewType.PHOTO : ReviewType.TEXT);
        if (images != null && !images.isEmpty()) {
            validateImages(images);
            uploadReviewImages(newReview, images);
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
    public void update(Long reviewId, UpdateReviewRequest req, List<MultipartFile> images) {
        Member loginMember = authUtil.getLoginMember();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));
        if (!loginMember.equals(review.getMember())) {
            throw new UnauthorizedAccessException(ErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
        }
        review.update(req.getContent(), req.getStarRating());
        review.setType(images != null && !images.isEmpty() ? ReviewType.PHOTO : ReviewType.TEXT);
        if (images != null && !images.isEmpty()) {
            validateImages(images);
            deleteReviewImages(review);
            uploadReviewImages(review, images);
        }
        reviewRepository.save(review);
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
            deleteReviewImages(review);
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

    private void uploadReviewImages(Review review, List<MultipartFile> images) {
        for (MultipartFile image : images) {
            S3Service.S3ImageInfo imageInfo = s3Service.uploadFile(FILE_TYPE, image);
            ReviewImage reviewImage = new ReviewImage(
                    imageInfo.imageUrl(), imageInfo.imageKey(), imageInfo.originFilename());
            reviewImage.attachReview(review);
        }
    }

    private void deleteReviewImages(Review review) {
        List<String> imageKeys = reviewImageRepository.findImageKeysByReviewId(review.getId());
        s3Service.deleteImagesAsync(imageKeys);
        reviewImageRepository.deleteAllByReviewId(review.getId());
        review.clearImages();
    }

    private void validateImages(List<MultipartFile> images) {
        validateImagesLimit(images);
        for (MultipartFile image : images) {
            imageValidator.validate(image);
        }
    }

    private void validateImagesLimit(List<MultipartFile> images) {
        if (images.size() > IMAGES_LIMIT) {
            throw new FileProcessingException(ErrorCode.IMAGES_LIMIT_EXCEEDED);
        }
    }
}

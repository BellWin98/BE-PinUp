package com.pinup.domain.review.service;

import com.pinup.domain.friend.service.FriendShipService;
import com.pinup.domain.place.dto.request.PlaceRequest;
import com.pinup.domain.review.dto.request.ReviewRequest;
import com.pinup.domain.review.dto.response.ReviewPreviewResponse;
import com.pinup.domain.review.dto.response.ReviewResponse;
import com.pinup.domain.member.entity.Member;
import com.pinup.domain.place.entity.Place;
import com.pinup.domain.review.entity.Review;
import com.pinup.domain.review.entity.ReviewImage;
import com.pinup.domain.review.entity.ReviewType;
import com.pinup.global.exception.ImagesLimitExceededException;
import com.pinup.global.exception.EntityNotFoundException;
import com.pinup.global.response.ErrorCode;
import com.pinup.global.config.s3.S3Service;
import com.pinup.global.common.AuthUtil;
import com.pinup.domain.member.repository.MemberRepository;
import com.pinup.domain.place.repository.PlaceRepository;
import com.pinup.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private static final String FILE_TYPE = "reviews";
    private static final int IMAGES_LIMIT = 3;

    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;
    private final FriendShipService friendShipService;
    private final AuthUtil authUtil;
    private final S3Service s3Service;

    @Transactional
    public String register(ReviewRequest reviewRequest, PlaceRequest placeRequest, List<MultipartFile> images) {
        Member loginMember = authUtil.getLoginMember();
        Place place = findOrCreatePlace(placeRequest);
        List<String> uploadedFileUrls = uploadImages(images);
        Review newReview = createReview(reviewRequest, loginMember, place, uploadedFileUrls);
        newReview.setType(images != null && !images.isEmpty() ? ReviewType.PHOTO : ReviewType.TEXT);
        reviewRepository.save(newReview);

        return place.getKakaoMapId();
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long reviewId) {
        Member currentUser = authUtil.getLoginMember();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));
        validateReviewAccess(currentUser, review);
        return ReviewResponse.from(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewPreviewResponse> getPhotoReviewPreviews() {
        Member currentUser = authUtil.getLoginMember();
        return reviewRepository.findAllByMemberAndType(currentUser, ReviewType.PHOTO).stream()
                .map(ReviewPreviewResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyTextReviewDetails() {
        Member currentUser = authUtil.getLoginMember();
        return reviewRepository.findAllByMemberAndType(currentUser, ReviewType.TEXT).stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewPreviewResponse> getMemberPhotoReviewPreviews(Long memberId) {
        Member currentUser = authUtil.getLoginMember();
        Member targetMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        validateFriendship(currentUser, targetMember);

        return reviewRepository.findAllByMemberAndType(targetMember, ReviewType.PHOTO).stream()
                .map(ReviewPreviewResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Double getMemberAverageRating(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        return reviewRepository.findAllByMember(member).stream()
                .mapToDouble(Review::getStarRating)
                .average()
                .orElse(0.0);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getMemberTextReviews(Long memberId) {
        Member currentUser = authUtil.getLoginMember();
        Member targetMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        validateFriendship(currentUser, targetMember);

        return reviewRepository.findAllByMemberAndType(targetMember, ReviewType.TEXT).stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 카카오맵 ID로 DB에 등록된 업체 정보 조회
     * DB에 업체 미등록 시, 업체 신규 생성
     */
    private Place findOrCreatePlace(PlaceRequest placeRequest) {
        String kakaoPlaceId = placeRequest.getKakaoPlaceId();

        return placeRepository.findByKakaoMapId(kakaoPlaceId)
                .orElseGet(() -> placeRepository.save(placeRequest.toEntity()));
    }

    /**
     * S3에 이미지 업로드
     * 이미지 최대 업로드 가능 갯수: 3장
     */
    private List<String> uploadImages(List<MultipartFile> images) {

        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }

        if (images.size() > IMAGES_LIMIT) {
            throw new ImagesLimitExceededException();
        }

        List<String> uploadedFileUrls = new ArrayList<>();

        for (MultipartFile multipartFile : images) {
            String uploadedFileUrl = s3Service.uploadFile(FILE_TYPE, multipartFile);
            uploadedFileUrls.add(uploadedFileUrl);
        }

        return uploadedFileUrls;
    }

    /**
     * 생성된 리뷰 엔티티에 작성자, 업체, 리뷰 이미지, 키워드 연결
     */
    private Review createReview(
            ReviewRequest reviewRequest,
            Member writer,
            Place place,
            List<String> uploadedFileUrls
    ) {

        Review newReview = reviewRequest.toEntity();
        newReview.attachMember(writer);
        newReview.attachPlace(place);

        // 리뷰 이미지와 키워드를 리뷰 엔티티에 연결
        for (String fileUrl : uploadedFileUrls) {
            ReviewImage reviewImage = new ReviewImage(fileUrl);
            reviewImage.attachReview(newReview);
        }

        return newReview;
    }

    private void validateReviewAccess(Member currentUser, Review review) {
        if (!review.getMember().equals(currentUser) &&
                !friendShipService.existsFriendship(currentUser, review.getMember())) {
            throw new EntityNotFoundException(ErrorCode.FRIEND_NOT_FOUND);
        }
    }

    private void validateFriendship(Member currentUser, Member targetMember) {
        if (!currentUser.equals(targetMember) &&
                !friendShipService.existsFriendship(currentUser, targetMember)) {
            throw new EntityNotFoundException(ErrorCode.FRIEND_NOT_FOUND);
        }
    }
}

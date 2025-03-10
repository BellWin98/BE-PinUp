package com.pinup.domain.review.dto.response;

import com.pinup.domain.review.entity.Review;
import com.pinup.domain.review.entity.ReviewImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
public class PhotoReviewResponse {

    @Schema(description = "리뷰 고유 ID")
    private Long reviewId;
    @Schema(description = "카카오맵 장소 ID")
    private String kakaoPlaceId;
    @Schema(description = "장소명")
    private String placeName;
    @Schema(description = "리뷰 내용")
    private String content;
    @Schema(description = "별점")
    private double starRating;
    @Schema(description = "리뷰 이미지 리스트")
    private List<String> reviewImageUrls;
    @Schema(description = "리뷰 작성일자")
    private String createdAt;
    @Schema(description = "장소 방문일자")
    private String visitedDate;

    public static PhotoReviewResponse from(Review review) {
        return PhotoReviewResponse.builder()
                .reviewId(review.getId())
                .kakaoPlaceId(review.getPlace().getKakaoPlaceId())
                .placeName(review.getPlace().getName())
                .content(review.getContent())
                .starRating(review.getStarRating())
                .reviewImageUrls(review.getReviewImages().stream()
                        .map(ReviewImage::getUrl)
                        .toList())
                .createdAt(DateTimeFormatter.ofPattern("yy.MM.dd").format(review.getCreatedAt()))
                .visitedDate(review.getVisitedDate())
                .build();
    }
}

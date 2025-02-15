package com.pinup.domain.place.dto.response;

import com.pinup.domain.place.entity.Place;
import com.pinup.domain.place.entity.PlaceCategory;
import com.pinup.domain.review.entity.Review;
import com.pinup.domain.review.entity.ReviewImage;
import com.pinup.global.common.Formatter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;

@Getter
@Builder
public class PlaceResponse {

    @Schema(description = "카카오맵 장소 고유 ID")
    private String kakaoPlaceId;

    @Schema(description = "장소명")
    private String name;

    @Schema(description = "평균 별점")
    private Double averageStarRating;

    @Schema(description = "리뷰 수")
    private int reviewCount;

    @Schema(description = "현재 위치에서 해당 장소까지 떨어진 거리(단위: km)")
    private String distance;

    @Schema(description = "장소의 위도")
    private double latitude;

    @Schema(description = "장소의 경도")
    private double longitude;

    @Schema(description = "장소 카테고리")
    private PlaceCategory placeCategory;

    @Schema(description = "리뷰 이미지 URL 리스트 (가장 먼저 등록된 리뷰 이미지 순서로 최대 3장)")
    private List<String> reviewImageUrls;

    @Schema(description = "리뷰 작성자 프로필 이미지 URL 리스트 (가장 최근에 리뷰 작성한 유저 순서로 최대 3장)")
    private List<String> reviewerProfileImageUrls;

    public static PlaceResponse from(Place place) {

        List<Review> reviews = place.getReviews();

        double averageStarRating = reviews.stream()
                .mapToDouble(Review::getStarRating)
                .average()
                .orElse(0.0);

        reviews.stream()
                .map(Review::getReviewImages)
                .flatMap(List::stream)
                .sorted(Comparator.comparing(ReviewImage::getCreatedAt))
                .limit(3)
                .map(ReviewImage::getUrl)
                .toList();

        return PlaceResponse.builder()
                .kakaoPlaceId(place.getKakaoPlaceId())
                .name(place.getName())
                .averageStarRating(Formatter.formatStarRating(averageStarRating))
                .reviewCount(reviews.size())
                .build()
    }
}

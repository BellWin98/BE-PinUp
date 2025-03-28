package com.pinup.domain.place.dto.response;

import com.pinup.domain.place.entity.Place;
import com.pinup.domain.place.entity.PlaceCategory;
import com.pinup.domain.review.dto.response.ReviewResponseDto;
import com.pinup.domain.review.entity.Review;
import com.pinup.global.common.Formatter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
@Builder
public class PlaceResponseDto {

    @Schema(description = "장소 고유 ID")
    private Long id;

    @Schema(description = "카카오맵에서 부여한 장소 고유 ID")
    private String kakaoPlaceId;

    @Schema(description = "장소명")
    private String name;

    @Schema(description = "장소 카테고리")
    private PlaceCategory placeCategory;

    @Schema(description = "장소 카테고리 설명")
    private String description;

    @Schema(description = "장소 카테고리 코드")
    private String categoryCode;

    @Schema(description = "주소")
    private String address;

    @Schema(description = "도로명 주소")
    private String roadAddress;

    @Schema(description = "위도")
    private Double latitude;

    @Schema(description = "경도")
    private Double longitude;

    @Schema(description = "본인 및 친구들의 리뷰 수")
    private int targetReviewCount;

    @Schema(description = "본인 및 친구들의 평균 평점")
    private Double targetAverageRating;

    @Schema(description = "전체 리뷰 수")
    private int overallReviewCount;

    @Schema(description = "전체 리뷰 평균 평점")
    private Double overallAverageRating;

    private List<ReviewResponseDto> matchedReviews;
    private Set<String> matchedKeywords;

    public static PlaceResponseDto of(Place place, List<Long> targetMemberIds) {
        PlaceCategory category = place.getPlaceCategory();
        List<Review> overallReviews = place.getReviews();
        List<Review> targetReviews = getTargetReviews(overallReviews, targetMemberIds);

        return PlaceResponseDto.builder()
                .id(place.getId())
                .kakaoPlaceId(place.getKakaoPlaceId())
                .name(place.getName())
                .placeCategory(category)
                .description(category.getDescription())
                .categoryCode(category.getCode())
                .address(place.getAddress())
                .roadAddress(place.getRoadAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .targetReviewCount(targetReviews.size())
                .targetAverageRating(getAverageRating(targetReviews))
                .overallReviewCount(overallReviews.size())
                .overallAverageRating(getAverageRating(overallReviews))
                .build();
    }

    private static List<Review> getTargetReviews(List<Review> reviews, List<Long> targetMemberIds) {
        return reviews.stream()
                .filter(review -> targetMemberIds.contains(review.getMember().getId()))
                .toList();
    }

    private static Double getAverageRating(List<Review> reviews) {
        double averageRating = reviews.stream()
                .mapToDouble(Review::getStarRating)
                .average()
                .orElse(0.0);

        return Formatter.formatStarRating(averageRating);
    }
}

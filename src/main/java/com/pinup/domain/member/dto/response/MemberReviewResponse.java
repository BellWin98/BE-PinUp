package com.pinup.domain.member.dto.response;

import com.pinup.domain.review.entity.Review;
import com.pinup.domain.review.entity.ReviewImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "유저가 작성한 리뷰 조회 응답 DTO")
public class MemberReviewResponse {

    @Schema(description = "유저 개인정보")
    private Long reviewId;
    @Schema(description = "유저 개인정보")
    private String kakaoPlaceId;
    @Schema(description = "유저 개인정보")
    private String placeName;
    @Schema(description = "유저 개인정보")
    private String content;
    @Schema(description = "유저 개인정보")
    private double starRating;
    @Schema(description = "유저 개인정보")
    private List<String> reviewImageUrls;
    @Schema(description = "유저 개인정보")
    private String createdAt;
    @Schema(description = "유저 개인정보")
    private String visitedDate;

    public static MemberReviewResponse of(Review review) {

        return MemberReviewResponse.builder()
                .reviewId(review.getId())
                .kakaoPlaceId(review.getPlace().getKakaoMapId())
                .placeName(review.getPlace().getName())
                .content(review.getContent())
                .starRating(review.getStarRating())
                .reviewImageUrls(review.getReviewImages().stream()
                        .map(ReviewImage::getUrl)
                        .toList())
                .createdAt(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(review.getCreatedAt()))
                .visitedDate(review.getVisitedDate())
                .build();
    }
}

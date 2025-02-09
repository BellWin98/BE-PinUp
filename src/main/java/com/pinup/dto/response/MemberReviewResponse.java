package com.pinup.dto.response;

import com.pinup.entity.Review;
import com.pinup.entity.ReviewImage;
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
public class MemberReviewResponse {

    private Long reviewId;
    private String kakaoPlaceId;
    private String placeName;
    private String content;
    private double starRating;
    private List<String> reviewImageUrls;
    private String createdAt;
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

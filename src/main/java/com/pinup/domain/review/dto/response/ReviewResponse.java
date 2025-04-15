package com.pinup.domain.review.dto.response;

import com.pinup.domain.review.entity.Review;
import com.pinup.domain.review.entity.ReviewType;
import com.pinup.global.common.Formatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private Long id;
    private String content;
    private Double starRating;
    private ReviewType type;
    private List<String> imageUrls;
    private String createdAt;
    private String visitedAt;

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .content(review.getContent())
                .starRating(review.getStarRating())
                .type(review.getType())
                .imageUrls(review.getReviewImages().stream()
                        .map(reviewImage -> reviewImage.getImage().getImageUrl())
                        .collect(Collectors.toList()))
                .createdAt(Formatter.formatDate(review.getCreatedAt()))
                .visitedAt(review.getVisitedDate())
                .build();
    }
}
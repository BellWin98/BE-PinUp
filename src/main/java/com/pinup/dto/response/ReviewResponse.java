package com.pinup.dto.response;

import com.pinup.entity.Review;
import com.pinup.entity.ReviewImage;
import com.pinup.enums.ReviewType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .content(review.getContent())
                .starRating(review.getStarRating())
                .type(review.getType())
                .imageUrls(review.getReviewImages().stream()
                        .map(ReviewImage::getUrl)
                        .collect(Collectors.toList()))
                .createdAt(review.getCreatedAt())
                .build();
    }
}
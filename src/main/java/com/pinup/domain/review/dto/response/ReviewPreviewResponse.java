package com.pinup.domain.review.dto.response;

import com.pinup.domain.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewPreviewResponse {
    private Long id;
    private String content;
    private String previewImageUrl;
    private LocalDateTime createdAt;

    public static ReviewPreviewResponse from(Review review) {
        return ReviewPreviewResponse.builder()
                .id(review.getId())
                .content(review.getContent())
                .previewImageUrl(review.getReviewImages().isEmpty() ? null :
                        review.getReviewImages().get(0).getUrl())
                .createdAt(review.getCreatedAt())
                .build();
    }
}